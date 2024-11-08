package com.sxyangsuper.exceptionunifier.processor;

import lombok.RequiredArgsConstructor;

import javax.annotation.processing.ProcessingEnvironment;

@RequiredArgsConstructor
public abstract class AbstractExceptionCodePrefixSupplier implements IExceptionCodePrefixSupplier {
    protected final ProcessingEnvironment processingEnv;
    protected final Logger logger;
}
