package com.sxyangsuper.exceptionunifier.processor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCLiteral;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sxyangsuper.exceptionunifier.base.IExceptionEnumAsserts;
import lombok.NoArgsConstructor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.sxyangsuper.exceptionunifier.base.Consts.EXCEPTION_CODE_SPLITTER;
import static com.sxyangsuper.exceptionunifier.processor.Consts.PROCESSOR_ARG_NAME_EXCEPTION_CODE_PREFIX;
import static com.sxyangsuper.exceptionunifier.processor.Consts.PROPERTY_DEFAULT_VALUE_ANNOTATION_PROCESSOR_DEBUG;
import static com.sxyangsuper.exceptionunifier.processor.Consts.PROPERTY_NAME_ANNOTATION_PROCESSOR_DEBUG;
import static javax.lang.model.SourceVersion.RELEASE_8;
import static javax.tools.Diagnostic.Kind.NOTE;

@NoArgsConstructor
@SupportedAnnotationTypes("*")
@SupportedSourceVersion(RELEASE_8)
public class ExceptionUnifierProcessor extends AbstractProcessor {
    private Messager messager;
    private JavacTrees javacTrees;
    private boolean isDebugEnabled;

    @Override
    public void init(final ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
        this.javacTrees = JavacTrees.instance(processingEnv);
        isDebugEnabled = Boolean.parseBoolean(
            System.getProperty(
                PROPERTY_NAME_ANNOTATION_PROCESSOR_DEBUG,
                PROPERTY_DEFAULT_VALUE_ANNOTATION_PROCESSOR_DEBUG
            )
        );
    }

    @Override
    public Set<String> getSupportedOptions() {
        return CollUtil.newHashSet(PROCESSOR_ARG_NAME_EXCEPTION_CODE_PREFIX);
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            this.note("Processing complete");
            return false;
        }

        this.debug(() -> "Start annotation processing...");

        final List<TypeElement> exceptionEnums = this.findExceptionEnums(roundEnv);
        if (CollUtil.isEmpty(exceptionEnums)) {
            return false;
        }

        this.note(String.format("Find exception %s exception enums", exceptionEnums.size()));

        exceptionEnums.forEach(ProcessorUtil::validateExceptionEnum);

        // prepare for prepending
        final List<Map.Entry<TypeElement, List<JCLiteral>>> exceptionEnumToEnumVariableCodeExpressionLists
            = this.getExceptionEnumToEnumVariableCodeExpressionLists(exceptionEnums);

        ProcessorUtil.prependExceptionSourceCode(exceptionEnumToEnumVariableCodeExpressionLists);

        ProcessorUtil.assertNoDuplicateExceptionCode(exceptionEnumToEnumVariableCodeExpressionLists);

        final String exceptionCodePrefix = this.getExceptionCodePrefix();
        ProcessorUtil.prependExceptionCodePrefix(exceptionCodePrefix, exceptionEnumToEnumVariableCodeExpressionLists);

        this.debug(() -> "End annotation processing...");

        return false;
    }

    private String getExceptionCodePrefix() {
        debug(() -> "Start getting exception code prefix");

        final IExceptionCodePrefixSupplier exceptionCodePrefixSupplier = ExceptionCodePrefixSupplierDetector.detect(processingEnv);

        debug(() -> String.format("Detect supplier %s is available", exceptionCodePrefixSupplier.getClass().getName()));

        final String exceptionCodePrefix = exceptionCodePrefixSupplier.get();

        if (StrUtil.isBlank(exceptionCodePrefix)) {
            throw new ExUnifierProcessException("Fail to get exception code prefix");
        }

        if (exceptionCodePrefix.contains(EXCEPTION_CODE_SPLITTER)) {
            throw new ExUnifierProcessException(
                String.format(
                    "Invalid exception code prefix %s, can not contain reserved character: %s",
                    exceptionCodePrefix,
                    EXCEPTION_CODE_SPLITTER
                )
            );
        }

        debug(() -> String.format(
            "End getting exception code prefix with result %s",
            exceptionCodePrefix
        ));
        return exceptionCodePrefix;
    }

    private List<Map.Entry<TypeElement, List<JCLiteral>>> getExceptionEnumToEnumVariableCodeExpressionLists(final List<TypeElement> exceptionEnums) {
        this.debug(() -> "Start getting exception enum to enum variable code expression lists");

        final List<Map.Entry<TypeElement, List<JCLiteral>>> exceptionEnumToEnumVariableCodeExpressionLists = exceptionEnums
            .stream()
            .map(
                (exceptionSourceAnnotatedExceptionEnum) ->
                    new AbstractMap.SimpleEntry<>(
                        exceptionSourceAnnotatedExceptionEnum,
                        this.getEnumVariableCodeExpressions(exceptionSourceAnnotatedExceptionEnum
                        )
                    )
            )
            .collect(Collectors.toList());

        this.debug(() -> "End getting exception enum to enum variable code expression lists");
        return exceptionEnumToEnumVariableCodeExpressionLists;
    }

    private List<JCLiteral> getEnumVariableCodeExpressions(final TypeElement exceptionSourceAnnotatedExceptionEnum) {
        final String targetQualifiedName = exceptionSourceAnnotatedExceptionEnum.getQualifiedName().toString();
        this.debug(() -> String.format("Start getting enum variable code expressions for %s",
            targetQualifiedName
        ));

        final JCTree.JCClassDecl targetClassDecl = javacTrees.getTree(exceptionSourceAnnotatedExceptionEnum);

        final List<JCLiteral> enumVariableCodeExpressions = new ArrayList<>();

        targetClassDecl.accept(new TreeTranslator() {
            @Override
            public void visitClassDef(final JCTree.JCClassDecl jcClassDecl) {
                // get exception code index in all fields
                final int codeFieldIndex = ProcessorUtil.getCodeIndex(jcClassDecl);

                for (final JCTree tree : jcClassDecl.defs) {
                    if (ProcessorUtil.isEnumVariable(tree)) {
                        final JCTree.JCVariableDecl jcVariableDecl = (JCTree.JCVariableDecl) tree;
                        final JCTree.JCNewClass jcNewClass = (JCTree.JCNewClass) jcVariableDecl.init;
                        final JCLiteral codeExpression = (JCLiteral) jcNewClass.args.get(codeFieldIndex);
                        enumVariableCodeExpressions.add(codeExpression);
                    }
                }
                super.visitClassDef(jcClassDecl);
            }
        });

        this.debug(() -> String.format("End getting enum variable code expressions for %s with total count %d",
            targetQualifiedName,
            enumVariableCodeExpressions.size()
        ));
        return enumVariableCodeExpressions;
    }

    private List<TypeElement> findExceptionEnums(final RoundEnvironment roundEnv) {
        this.debug(() -> "Start finding exception enums...");
        final Types typeUtils = processingEnv.getTypeUtils();

        final String exceptionEnumAssertsQualifiedName = IExceptionEnumAsserts.class.getName();

        final List<TypeElement> exceptionEnumTypeElements = roundEnv.getRootElements()
            .stream()
            .filter(
                element -> ProcessorUtil.isChildEnumOfTargetInterface(typeUtils, element, exceptionEnumAssertsQualifiedName)
            )
            .map(element -> (TypeElement) element)
            .collect(Collectors.toList());

        this.debug(() -> String.format("End finding exception enums, total count is: %s", exceptionEnumTypeElements.size()));
        return exceptionEnumTypeElements;
    }

    private void debug(final Supplier<String> messageSupplier) {
        if (this.isDebugEnabled) {
            this.messager.printMessage(NOTE, String.format("[DEBUG] %s %s", Consts.LOG_PREFIX, messageSupplier.get()));
        }
    }

    private void note(final String message) {
        this.messager.printMessage(NOTE, String.format("[NOTE] %s %s", Consts.LOG_PREFIX, message));
    }
}
