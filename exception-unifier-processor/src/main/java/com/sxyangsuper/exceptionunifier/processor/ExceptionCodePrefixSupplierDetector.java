package com.sxyangsuper.exceptionunifier.processor;

import lombok.NoArgsConstructor;

import javax.annotation.processing.ProcessingEnvironment;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ExceptionCodePrefixSupplierDetector {

    public static IExceptionCodePrefixSupplier detect(final ProcessingEnvironment processingEnv) {
        return new ConfigurableExceptionCodePrefixSupplier(processingEnv);
    }
}
