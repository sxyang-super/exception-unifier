package io.github.sxyangsuper.exceptionunifier.processor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import io.github.sxyangsuper.exceptionunifier.base.IExceptionEnumAsserts;
import lombok.NoArgsConstructor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static io.github.sxyangsuper.exceptionunifier.base.Consts.EXCEPTION_CODE_SPLITTER;
import static io.github.sxyangsuper.exceptionunifier.processor.Consts.ANNOTATION_PACKAGE_NAME_PREFIX;
import static javax.lang.model.SourceVersion.RELEASE_8;
import static javax.lang.model.element.ElementKind.ENUM;

@NoArgsConstructor
@SupportedAnnotationTypes("*")
@SupportedSourceVersion(RELEASE_8)
public class ExceptionUnifierProcessor extends AbstractProcessor {
    private Logger logger;
    private static final String EXCEPTION_ENUM_ASSERTS_QUALIFIED_NAME = IExceptionEnumAsserts.class.getName();

    @Override
    public void init(final ProcessingEnvironment processingEnv) {
        final ProcessingEnvironment unWrappedProcessingEnv = UncheckedUtil.jbUnwrapProcessingEnvironment(processingEnv);
        super.init(unWrappedProcessingEnv);
        this.logger = new Logger(unWrappedProcessingEnv.getMessager());
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

        this.logger.note(String.format("Find %s exception enums in total", exceptionEnums.size()));

        validateExceptionEnums(exceptionEnums);

        this.logger.debug(() -> "End annotation processing...");

        return this.areAllAnnotationsFromExceptionUnifier(annotations);
    }

    private void validateExceptionEnums(final List<TypeElement> exceptionEnums) {
        final Types typeUtils = processingEnv.getTypeUtils();

        exceptionEnums.forEach((exceptionEnum) -> {
            if (!ProcessorUtil.isDescendantOfTargetInterface(typeUtils, exceptionEnum, EXCEPTION_ENUM_ASSERTS_QUALIFIED_NAME)) {
                throw new ExUnifierProcessException(
                    String.format(
                        "Invalid exception enum %s, exception enum mast be the descendant of interface: \"%s\"",
                        exceptionEnum.getQualifiedName(),
                        EXCEPTION_ENUM_ASSERTS_QUALIFIED_NAME
                    )
                );
            }

            final String value = exceptionEnum.getAnnotation(ExceptionSource.class).value();
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
        });

    }

    private boolean areAllAnnotationsFromExceptionUnifier(final Set<? extends TypeElement> annotations) {
        // true if only exception unifier annotation
        return annotations
            .stream()
            .allMatch(annotation -> annotation.getQualifiedName().toString().startsWith(ANNOTATION_PACKAGE_NAME_PREFIX));
    }

    private List<TypeElement> findExceptionEnums(final RoundEnvironment roundEnv) {
        this.logger.debug(() -> "Start finding exception enums...");
        final List<TypeElement> exceptionEnumTypeElements = roundEnv.getRootElements()
            .stream()
            .filter(
                element -> element.getKind() == ENUM && element.getAnnotation(ExceptionSource.class) != null
            )
            .map(element -> (TypeElement) element)
            .collect(Collectors.toList());

        this.logger.debug(() -> String.format("End finding exception enums, total count is: %s", exceptionEnumTypeElements.size()));
        return exceptionEnumTypeElements;
    }
}
