package com.sxyangsuper.exceptionunifier.processor;

import javax.annotation.processing.ProcessingEnvironment;

import static com.sxyangsuper.exceptionunifier.processor.Consts.PROCESSOR_ARG_NAME_EXCEPTION_CODE_PREFIX;

public class ConfigurableExceptionCodePrefixSupplier extends AbstractExceptionCodePrefixSupplier {

    public ConfigurableExceptionCodePrefixSupplier(final ProcessingEnvironment processingEnv, final Logger logger) {
        super(processingEnv, logger);
    }

    @Override
    public String get() {
        // get from compile args
        return this.processingEnv.getOptions().get(PROCESSOR_ARG_NAME_EXCEPTION_CODE_PREFIX);
    }

    @Override
    public String getModuleId() {
        // dont need module id
        return null;
    }
}
