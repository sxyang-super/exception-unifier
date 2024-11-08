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
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sxyangsuper.exceptionunifier.base.Consts.EXCEPTION_CODE_SPLITTER;
import static com.sxyangsuper.exceptionunifier.processor.Consts.ANNOTATION_PACKAGE_NAME_PREFIX;
import static javax.lang.model.SourceVersion.RELEASE_8;

@NoArgsConstructor
@SupportedAnnotationTypes("*")
@SupportedSourceVersion(RELEASE_8)
public class ExceptionUnifierProcessor extends AbstractProcessor {
    private JavacTrees javacTrees;
    private Logger logger;

    @Override
    public void init(final ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.javacTrees = JavacTrees.instance(processingEnv);
        this.logger = new Logger(processingEnv.getMessager());
    }

    @Override
    public Set<String> getSupportedOptions() {
        return CollUtil.newHashSet(
            Consts.PROCESSOR_ARG_NAME_EXCEPTION_CODE_PREFIX,
            Consts.PROCESSOR_ARG_NAME_REMOTE_BASE_URL,
            Consts.PROCESSOR_ARG_NAME_MODULE_ID
        );
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            this.logger.note("Processing complete");
            return false;
        }

        this.logger.debug(() -> "Start annotation processing...");

        if (CollUtil.isEmpty(annotations)) {
            return false;
        }

        final List<TypeElement> exceptionEnums = this.findExceptionEnums(roundEnv);
        if (CollUtil.isEmpty(exceptionEnums)) {
            return false;
        }

        this.logger.note(String.format("Find exception %s exception enums", exceptionEnums.size()));

        exceptionEnums.forEach(ProcessorUtil::validateExceptionEnum);

        // prepare for prepending
        final Map<TypeElement, List<ExceptionCodeExpressions>> exceptionEnumToExceptionCodeExpressionsLists
            = this.getExceptionEnumToExceptionCodeExpressionsLists(exceptionEnums);

        ProcessorUtil.validateExceptionCodeExpressionsList(exceptionEnumToExceptionCodeExpressionsLists);

        ProcessorUtil.prependExceptionSourceCode(exceptionEnumToExceptionCodeExpressionsLists);

        ProcessorUtil.assertNoDuplicateExceptionCode(exceptionEnumToExceptionCodeExpressionsLists);

        final IExceptionCodePrefixSupplier exceptionCodePrefixSupplier = ExceptionCodePrefixSupplierDetector.detect(this.processingEnv, this.logger);

        final String exceptionCodePrefix = this.getExceptionCodePrefix(exceptionCodePrefixSupplier);

        ProcessorUtil.prependExceptionCodePrefix(exceptionCodePrefix, exceptionEnumToExceptionCodeExpressionsLists);

        final List<ExceptionCodeExpressions> exceptionCodeExpressionsList = exceptionEnumToExceptionCodeExpressionsLists
            .values()
            .stream()
            .flatMap(List::stream)
            .collect(Collectors.toList());

        exceptionCodePrefixSupplier.collectExceptionCodes(exceptionCodeExpressionsList);

        this.logger.debug(() -> "End annotation processing...");

        return this.onlyExceptionUnifierAnnotation(annotations);
    }

    private boolean onlyExceptionUnifierAnnotation(final Set<? extends TypeElement> annotations) {
        // true if only exception unifier annotation
        return annotations
            .stream()
            .allMatch(annotation -> annotation.getQualifiedName().toString().startsWith(ANNOTATION_PACKAGE_NAME_PREFIX));
    }

    private String getExceptionCodePrefix(final IExceptionCodePrefixSupplier exceptionCodePrefixSupplier) {
        this.logger.debug(() -> "Start getting exception code prefix");

        this.logger.debug(() -> String.format("Detect supplier %s is available", exceptionCodePrefixSupplier.getClass().getName()));

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

        this.logger.debug(() -> String.format(
            "End getting exception code prefix with result %s",
            exceptionCodePrefix
        ));
        return exceptionCodePrefix;
    }

    private Map<TypeElement, List<ExceptionCodeExpressions>> getExceptionEnumToExceptionCodeExpressionsLists(final List<TypeElement> exceptionEnums) {
        this.logger.debug(() -> "Start getting exception enum to enum variable code expression lists");

        final Map<TypeElement, List<ExceptionCodeExpressions>> exceptionEnumToEnumVariableCodeExpressionLists = exceptionEnums
            .stream()
            .collect(
                Collectors.toMap(
                    exceptionSourceAnnotatedExceptionEnum -> exceptionSourceAnnotatedExceptionEnum,
                    this::getEnumVariableCodeExpressions
                )
            );

        this.logger.debug(() -> "End getting exception enum to enum variable code expression lists");
        return exceptionEnumToEnumVariableCodeExpressionLists;
    }

    private List<ExceptionCodeExpressions> getEnumVariableCodeExpressions(final TypeElement exceptionSourceAnnotatedExceptionEnum) {
        final String targetQualifiedName = exceptionSourceAnnotatedExceptionEnum.getQualifiedName().toString();
        this.logger.debug(() -> String.format("Start getting enum variable code expressions for %s",
            targetQualifiedName
        ));

        final JCTree.JCClassDecl targetClassDecl = javacTrees.getTree(exceptionSourceAnnotatedExceptionEnum);

        final List<ExceptionCodeExpressions> enumVariableCodeExpressions = new ArrayList<>();

        targetClassDecl.accept(new TreeTranslator() {
            @Override
            public void visitClassDef(final JCTree.JCClassDecl jcClassDecl) {
                // get exception code index in all fields
                final int codeFieldIndex = ProcessorUtil.getCodeArgIndex(jcClassDecl);
                final int messageFieldIndex = ProcessorUtil.getMessageArgIndex(jcClassDecl);

                for (final JCTree tree : jcClassDecl.defs) {
                    if (ProcessorUtil.isEnumVariable(tree)) {
                        final JCTree.JCVariableDecl jcVariableDecl = (JCTree.JCVariableDecl) tree;
                        final JCTree.JCNewClass jcNewClass = (JCTree.JCNewClass) jcVariableDecl.init;
                        final JCLiteral codeExpression = (JCLiteral) jcNewClass.args.get(codeFieldIndex);
                        final JCLiteral messageExpression = (JCLiteral) jcNewClass.args.get(messageFieldIndex);
                        enumVariableCodeExpressions.add(
                            new ExceptionCodeExpressions(codeExpression, messageExpression)
                        );
                    }
                }
                super.visitClassDef(jcClassDecl);
            }
        });

        this.logger.debug(() -> String.format("End getting enum variable code expressions for %s with total count %d",
            targetQualifiedName,
            enumVariableCodeExpressions.size()
        ));
        return enumVariableCodeExpressions;
    }

    private List<TypeElement> findExceptionEnums(final RoundEnvironment roundEnv) {
        this.logger.debug(() -> "Start finding exception enums...");
        final Types typeUtils = processingEnv.getTypeUtils();

        final String exceptionEnumAssertsQualifiedName = IExceptionEnumAsserts.class.getName();

        final List<TypeElement> exceptionEnumTypeElements = roundEnv.getRootElements()
            .stream()
            .filter(
                element -> ProcessorUtil.isChildEnumOfTargetInterface(typeUtils, element, exceptionEnumAssertsQualifiedName)
            )
            .map(element -> (TypeElement) element)
            .collect(Collectors.toList());

        this.logger.debug(() -> String.format("End finding exception enums, total count is: %s", exceptionEnumTypeElements.size()));
        return exceptionEnumTypeElements;
    }
}
