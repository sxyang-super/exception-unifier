package com.sxyangsuper.exceptionunifier.processor;

import cn.hutool.core.util.StrUtil;
import com.sun.source.tree.Tree.Kind;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import lombok.NoArgsConstructor;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.sxyangsuper.exceptionunifier.base.Consts.EXCEPTION_CODE_SPLITTER;
import static com.sxyangsuper.exceptionunifier.processor.Consts.JC_TREE_PREFIX_ENUM_VARIABLE;
import static com.sxyangsuper.exceptionunifier.processor.Consts.JC_VARIABLE_NAME_CODE;
import static javax.lang.model.element.ElementKind.ENUM;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
class ProcessorUtil {

    /* default */
    static boolean isChildEnumOfTargetInterface(
        final Types typeUtils,
        final Element element,
        final String targetInterfaceQualifiedName
    ) {
        return element.getKind() == ENUM && isOrExtendingTargetInterface(
            typeUtils,
            (TypeElement) element,
            targetInterfaceQualifiedName
        );
    }

    private static boolean isOrExtendingTargetInterface(
        final Types typeUtils,
        final TypeElement interfaceTypeElement,
        final String targetInterfaceQualifiedName
    ) {
        final String interfaceQualifiedName = interfaceTypeElement.getQualifiedName().toString();
        if (interfaceQualifiedName.equals(targetInterfaceQualifiedName)) {
            return true;
        }

        final List<? extends TypeMirror> superInterfaceTypes = interfaceTypeElement.getInterfaces();

        for (final TypeMirror superInterfaceType : superInterfaceTypes) {
            final TypeElement superInterfaceTypeElement = (TypeElement) typeUtils.asElement(superInterfaceType);
            if (isOrExtendingTargetInterface(typeUtils, superInterfaceTypeElement, targetInterfaceQualifiedName)) {
                return true;
            }
        }

        return false;
    }

    /* default */
    static int getCodeIndex(final JCClassDecl jcClassDecl) {
        final List<JCTree> nonEnumVariables = jcClassDecl.defs
            .stream()
            .filter(ProcessorUtil::isNotEnumVariable)
            .collect(Collectors.toList());

        for (int i = 0; i < nonEnumVariables.size(); i++) {
            final JCVariableDecl variableDecl = (JCVariableDecl) nonEnumVariables.get(i);
            if (JC_VARIABLE_NAME_CODE.equals(variableDecl.name.toString())) {
                return i;
            }
        }

        throw new ExUnifierProcessException(
            String.format("Can not find %s in %s", JC_VARIABLE_NAME_CODE, jcClassDecl.getSimpleName().toString())
        );
    }

    private static boolean isNotEnumVariable(final JCTree tree) {
        return tree.getKind().equals(Kind.VARIABLE)
            && !tree.toString().startsWith(JC_TREE_PREFIX_ENUM_VARIABLE);
    }

    /* default */
    static boolean isEnumVariable(final JCTree tree) {
        return tree.getKind().equals(Kind.VARIABLE)
            && tree.toString().startsWith(JC_TREE_PREFIX_ENUM_VARIABLE);
    }

    /* default */
    static void prependExceptionCodePrefix(final String exceptionCodePrefix, final List<Map.Entry<TypeElement, List<JCTree.JCLiteral>>> exceptionEnumToEnumVariableCodeExpressionLists) {
        exceptionEnumToEnumVariableCodeExpressionLists
            .stream()
            .map(Map.Entry::getValue)
            .flatMap(List::stream)
            .forEach(variableCodeExpression ->
                variableCodeExpression.value =
                    String.join(
                        EXCEPTION_CODE_SPLITTER,
                        exceptionCodePrefix,
                        variableCodeExpression.value.toString()
                    )
            );
    }

    /* default */
    static void assertNoDuplicateExceptionCode(final List<Map.Entry<TypeElement, List<JCTree.JCLiteral>>> exceptionEnumToEnumVariableCodeExpressionLists) {
        exceptionEnumToEnumVariableCodeExpressionLists
            .stream()
            .map(Map.Entry::getValue)
            .flatMap(List::stream)
            .map(expression -> expression.value.toString())
            // group by exception count, get appear times
            .collect(Collectors.toMap(exceptionCode -> exceptionCode, exceptionCode -> 1, Integer::sum))
            .entrySet()
            .stream()
            .filter(exceptionCodeToAppearTime -> exceptionCodeToAppearTime.getValue() > 1)
            .findFirst()
            .ifPresent(exceptionCodeToAppearTime -> {
                throw new ExUnifierProcessException(
                    String.format(
                        "Duplicate exception code %s",
                        exceptionCodeToAppearTime.getKey()
                    )
                );
            });
    }

    /* default */
    static void validateExceptionEnum(final TypeElement exceptionEnum) {
        final ExceptionSource exceptionSource = exceptionEnum.getAnnotation(ExceptionSource.class);
        if (exceptionSource == null) {
            throw new ExUnifierProcessException(String.format(
                "Invalid exception enum %s, not annotated with %s",
                exceptionEnum.getQualifiedName(),
                ExceptionSource.class.getSimpleName()
            ));
        }

        final String value = exceptionSource.value();
        if (StrUtil.isBlank(value)) {
            throw new ExUnifierProcessException(
                String.format(
                    "Invalid exception enum %s, exception source value can not be blank",
                    exceptionEnum.getQualifiedName()
                )
            );
        }

        if (value.contains(EXCEPTION_CODE_SPLITTER)) {
            throw new ExUnifierProcessException(
                String.format(
                    "Invalid exception enum %s, exception source value can not contain reserved character: \"%s\"",
                    exceptionEnum.getQualifiedName(),
                    EXCEPTION_CODE_SPLITTER
                )
            );
        }
    }

    /* default */
    static void prependExceptionSourceCode(final List<Map.Entry<TypeElement, List<JCTree.JCLiteral>>> exceptionEnumToEnumVariableCodeExpressionLists) {
        exceptionEnumToEnumVariableCodeExpressionLists
            .forEach(enumToEnumVariableCodeExpression -> prependExceptionSourceCode(
                enumToEnumVariableCodeExpression.getKey(),
                enumToEnumVariableCodeExpression.getValue()
            ));
    }

    private static void prependExceptionSourceCode(final TypeElement exceptionEnum, final List<JCTree.JCLiteral> variableCodeExpressions) {
        final ExceptionSource exceptionSource = exceptionEnum.getAnnotation(ExceptionSource.class);
        final String exceptionSourceCode = exceptionSource.value();

        variableCodeExpressions
            .forEach(variableCodeExpression ->
                variableCodeExpression.value =
                    String.join(
                        EXCEPTION_CODE_SPLITTER,
                        exceptionSourceCode,
                        variableCodeExpression.value.toString()
                    )
            );
    }
}
