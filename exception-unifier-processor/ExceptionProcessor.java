package com.sxyangsuper.exceptionunifier.processor;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import com.google.auto.service.AutoService;
import com.qsy.common.exception.IExceptionCode;
import com.qsy.common.exception.IExceptionCodeAsserts;
import com.qsy.common.exception.data.ModuleExceptionCodeUtil;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.qsy.common.exception.annotation.processor.ExceptionProcessorConst.*;
import static com.qsy.common.exception.annotation.processor.ExceptionProcessorConst.FILE_NAME_POM_XML;
import static com.qsy.common.exception.annotation.processor.ExceptionProcessorConst.JC_TREE_PREFIX_ENUM_VARIABLE;
import static com.qsy.common.exception.annotation.processor.ExceptionProcessorConst.JC_VARIABLE_NAME_CODE;
import static com.qsy.common.exception.annotation.processor.ExceptionProcessorConst.OPTION_KEY_PROJECT_ROOT_PATH;
import static com.qsy.common.exception.annotation.processor.ExceptionProcessorConst.POM_ELEMENT_ARTIFACT_ID;
import static com.qsy.common.exception.annotation.processor.ExceptionProcessorConst.POM_ELEMENT_GROUP_ID;
import static com.qsy.common.exception.annotation.processor.ExceptionProcessorConst.POM_ELEMENT_PARENT;
import static com.qsy.common.exception.annotation.processor.ExceptionProcessorConst.PROCESSOR_EXCEPTION_CODES_SUPPLIER;
import static com.qsy.common.exception.annotation.processor.ExceptionProcessorConst.PROCESSOR_OPTION_CONFIG_DIRECTION;

@SuppressWarnings("unused")
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions({OPTION_KEY_PROJECT_ROOT_PATH})
public class ExceptionProcessor extends AbstractProcessor {
    private JavacTrees trees;
    private Messager messager;
    private Map<String, String> options;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return CollectionUtil.newHashSet(ExceptionSource.class.getName());
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.trees = JavacTrees.instance(processingEnv);
        messager = processingEnv.getMessager();
        options = processingEnv.getOptions();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            processExceptionSource(roundEnv);
        } catch (ProcessException e) {
            messager.printMessage(e.getKind(), e.getMsg(), e.getE());
        }
        return true;
    }

    private void processExceptionSource(RoundEnvironment roundEnv) {
        Map<Name, ExceptionSource> exceptionCodeQualifiedNameToExceptionSources =
            getExceptionCodeQualifiedNameToExceptionSources(roundEnv);

        Map<TypeElement, ExceptionSource> exceptionCodeEnumToExceptionSources = roundEnv
            .getRootElements().stream()
            .map(element -> {
                TypeElement typeElement = (TypeElement) element;
                return new AbstractMap.SimpleEntry<>(
                    typeElement,
                    getExceptionSourceIfImplement(exceptionCodeQualifiedNameToExceptionSources, typeElement)
                );
            })
            .filter(entry -> entry.getValue().isPresent())
            .collect(Collectors.toMap(
                AbstractMap.SimpleEntry::getKey,
                simpleEntry -> simpleEntry.getValue().get()
            ));

        if (CollectionUtil.isNotEmpty(exceptionCodeEnumToExceptionSources)) {
            validateOptions(options);

            exceptionCodeEnumToExceptionSources.forEach((typeElement, exceptionSource) ->
                prependEnumCode(typeElement, getModuleExceptionCode(), exceptionSource.value().getCode()));
        }

    }

    private String getModuleExceptionCode() {
        String fullModuleId = getFullModuleIdFromPom();
        return ModuleExceptionCodeUtil.findModuleExceptionCode(PROCESSOR_EXCEPTION_CODES_SUPPLIER, fullModuleId)
            .orElseThrow(() -> new ProcessException()
                .setKind(Diagnostic.Kind.ERROR)
                .setMsg("can not find exception code for module " + fullModuleId));
    }

    private String getFullModuleIdFromPom() {
        // TODO find a automate way to get pom.xml
        Document document = XmlUtil.readXML(options.get(OPTION_KEY_PROJECT_ROOT_PATH) + FILE_NAME_POM_XML);
        org.w3c.dom.Element project = XmlUtil.getRootElement(document);
        String artifactId = XmlUtil.getElement(project, POM_ELEMENT_ARTIFACT_ID).getTextContent();
        String groupId = Optional.ofNullable(XmlUtil.getElement(project, POM_ELEMENT_GROUP_ID))
            .map(Node::getTextContent)
            .orElse(
                XmlUtil.getElement(
                    XmlUtil.getElement(project, POM_ELEMENT_PARENT),
                    POM_ELEMENT_GROUP_ID
                ).getTextContent()
            );
        return String.join(".", groupId, artifactId);
    }

    private Map<Name, ExceptionSource> getExceptionCodeQualifiedNameToExceptionSources(RoundEnvironment roundEnv) {
        return roundEnv
            .getElementsAnnotatedWith(ExceptionSource.class)
            .stream()
            .distinct()
            .filter(this::isValidExceptionSourceAnnotatedElement)
            .map(element -> (TypeElement) element)
            .collect(Collectors.toMap(
                TypeElement::getQualifiedName,
                typeElement -> typeElement.getAnnotation(ExceptionSource.class)
            ));
    }

    private void prependEnumCode(TypeElement typeElement, String moduleExceptionCode, String exceptionSourceCode) {
        messager.printMessage(Diagnostic.Kind.NOTE,
            "compiling enum " + typeElement.getQualifiedName() +
                " by module exception code " + moduleExceptionCode +
                ", exception source code " + exceptionSourceCode,
            typeElement);

        trees.getTree(typeElement).accept(new TreeTranslator() {
            @Override
            public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                int codeIndex = getCodeIndex(jcClassDecl.defs);
                List<JCTree.JCLiteral> enumVariableExpression = jcClassDecl.defs.stream()
                    .filter(tree -> isEnumVariable(tree))
                    .map(tree -> {
                        JCTree.JCVariableDecl jcVariableDecl = (JCTree.JCVariableDecl) tree;
                        JCTree.JCNewClass jcNewClass = (JCTree.JCNewClass) jcVariableDecl.init;
                        return (JCTree.JCLiteral) jcNewClass.args.get(codeIndex);
                    })
                    .collect(Collectors.toList());

                List<Map.Entry<String, List<JCTree.JCLiteral>>> duplicateCodeToExpressions = enumVariableExpression.stream()
                    .collect(Collectors.groupingBy(expression -> expression.value.toString()))
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().size() > 1)
                    .collect(Collectors.toList());

                if (CollectionUtil.isNotEmpty(duplicateCodeToExpressions)) {
                    throw new DuplicateEnumCodeProcessException()
                        .setDuplicateCodeToExpressions(duplicateCodeToExpressions)
                        .setKind(Diagnostic.Kind.ERROR)
                        .setE(typeElement);
                }

                for (JCTree tree : jcClassDecl.defs) {
                    if (isEnumVariable(tree)) {
                        JCTree.JCVariableDecl jcVariableDecl = (JCTree.JCVariableDecl) tree;
                        JCTree.JCNewClass jcNewClass = (JCTree.JCNewClass) jcVariableDecl.init;
                        JCTree.JCLiteral codeExpression = (JCTree.JCLiteral) jcNewClass.args.get(codeIndex);
                        codeExpression.value = exceptionSourceCode + moduleExceptionCode + codeExpression.value;
                    }
                }
                super.visitClassDef(jcClassDecl);
            }
        });
    }

    private Optional<ExceptionSource> getExceptionSourceIfImplement(
        Map<Name, ExceptionSource> qualifiedNameToExceptionSources, TypeElement typeElement) {
        if (!ElementKind.ENUM.equals(typeElement.getKind())) {
            return Optional.empty();
        }
        return getOnlyImplementedInterfaceQualifiedIn(typeElement, qualifiedNameToExceptionSources.keySet())
            .map(qualifiedNameToExceptionSources::get);
    }

    private Optional<Name> getOnlyImplementedInterfaceQualifiedIn(TypeElement typeElement, Set<Name> superInterfaceQualifiedNames) {
        Set<Name> implementedInterfaceNames = findImplementedInterfaceNames(typeElement);

        if (CollectionUtil.isEmpty(implementedInterfaceNames)) {
            return Optional.empty();
        }

        List<Name> implementedExceptionSourceAnnotatedInterfaceQualifiedNames = implementedInterfaceNames.stream()
            .filter(superInterfaceQualifiedNames::contains)
            .collect(Collectors.toList());

        if (CollectionUtil.isEmpty(implementedExceptionSourceAnnotatedInterfaceQualifiedNames)) {
            return Optional.empty();
        }
        if (implementedExceptionSourceAnnotatedInterfaceQualifiedNames.size() > 1) {
            throw new ProcessException()
                .setKind(Diagnostic.Kind.ERROR)
                .setMsg("expected implement single interface annotated with @" + ExceptionSource.class.getSimpleName() +
                    ", but get " + implementedExceptionSourceAnnotatedInterfaceQualifiedNames.size() + ":\n" +
                    generateImplementMultipleExceptionSourceAnnotatedInterfaceErrorMessage(implementedExceptionSourceAnnotatedInterfaceQualifiedNames))
                .setE(typeElement);
        }

        return Optional.of(implementedExceptionSourceAnnotatedInterfaceQualifiedNames.get(0));
    }

    private String generateImplementMultipleExceptionSourceAnnotatedInterfaceErrorMessage(List<Name> names) {
        return names.stream().map(name -> "\t- " + name.toString()).collect(Collectors.joining("\n"));
    }

    private Set<Name> findImplementedInterfaceNames(TypeElement typeElement) {
        List<TypeElement> superInterfaces = typeElement.getInterfaces()
            .stream()
            .map(superInterface -> (TypeElement) ((DeclaredType) superInterface).asElement())
            .collect(Collectors.toList());

        if (CollectionUtil.isEmpty(superInterfaces)) {
            return Collections.emptySet();
        }

        Set<Name> superInterfaceQualifiedNames = superInterfaces.stream()
            .map(TypeElement::getQualifiedName)
            .collect(Collectors.toSet());

        List<Name> supersSuperInterfaceQualifiedNames = superInterfaces.stream()
            .map(this::findImplementedInterfaceNames)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

        superInterfaceQualifiedNames.addAll(supersSuperInterfaceQualifiedNames);
        return superInterfaceQualifiedNames;
    }

    private boolean isValidExceptionSourceAnnotatedElement(Element element) {
        if (!ElementKind.INTERFACE.equals(element.getKind())) {
            return false;
        }
        List<? extends TypeMirror> interfaces = ((TypeElement) element).getInterfaces();
        if (CollectionUtil.isEmpty(interfaces)) {
            return false;
        }
        return interfaces.stream().anyMatch(this::isIExceptionCode);
    }

    private Optional<TypeElement> findSuperExceptionSourceAnnotatedElement(Element element) {
        if (!ElementKind.INTERFACE.equals(element.getKind())) {
            return Optional.empty();
        }

        List<? extends TypeMirror> interfaces = ((TypeElement) element).getInterfaces();
        if (CollectionUtil.isEmpty(interfaces)) {
            return Optional.empty();
        }

        List<TypeElement> superExceptionSourceAnnotatedInterfaces = interfaces.stream()
            .map(typeMirror -> {
                DeclaredType declaredType = (DeclaredType) typeMirror;
                return (TypeElement) declaredType.asElement();
            })
            .filter(this::isValidExceptionSourceAnnotatedElement)
            .collect(Collectors.toList());

        if (CollectionUtil.isEmpty(superExceptionSourceAnnotatedInterfaces)) {
            return Optional.empty();
        }

        if (superExceptionSourceAnnotatedInterfaces.size() > 1) {
            throw new ProcessException()
                .setKind(Diagnostic.Kind.ERROR)
                .setMsg("expected extend single interface annotated with @" + ExceptionSource.class.getSimpleName() +
                    ", but get " + superExceptionSourceAnnotatedInterfaces.size() + ":\n" +
                    generateImplementMultipleExceptionSourceAnnotatedInterfaceErrorMessage(
                        superExceptionSourceAnnotatedInterfaces.stream().map(TypeElement::getQualifiedName)
                            .collect(Collectors.toList())
                    )
                )
                .setE(element);
        }
        return Optional.of(superExceptionSourceAnnotatedInterfaces.get(0));
    }

    private boolean isIExceptionCode(TypeMirror typeMirror) {
        DeclaredType declaredType = (DeclaredType) typeMirror;
        TypeElement element = (TypeElement) declaredType.asElement();
        String interfaceQualifiedName = element.getQualifiedName().toString();
        return IExceptionCode.class.getName().equals(interfaceQualifiedName)
                || IExceptionCodeAsserts.class.getName().equals(interfaceQualifiedName);
    }

    private int getCodeIndex(List<JCTree> defs) {
        List<JCTree> nonEnumVariables = defs.stream().filter(this::isNonEnumVariable)
            .collect(Collectors.toList());
        for (int i = 0; i < nonEnumVariables.size(); i++) {
            JCTree.JCVariableDecl variableDecl = (JCTree.JCVariableDecl) nonEnumVariables.get(i);
            if (JC_VARIABLE_NAME_CODE.equals(variableDecl.name.toString())) {
                return i;
            }
        }
        return -1;
    }

    private boolean isNonEnumVariable(JCTree tree) {
        return tree.getKind().equals(Tree.Kind.VARIABLE)
            && !tree.getTree().toString().startsWith(JC_TREE_PREFIX_ENUM_VARIABLE);
    }

    private boolean isEnumVariable(JCTree tree) {
        return tree.getKind().equals(Tree.Kind.VARIABLE)
            && tree.getTree().toString().startsWith(JC_TREE_PREFIX_ENUM_VARIABLE);
    }

    private void validateOptions(Map<String, String> options) {
        if (StrUtil.isBlank(options.get(OPTION_KEY_PROJECT_ROOT_PATH))) {
            throw new ProcessException()
                .setKind(Diagnostic.Kind.ERROR)
                .setMsg(PROCESSOR_OPTION_CONFIG_DIRECTION);
        }
    }
}
