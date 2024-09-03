package com.sxyangsuper.exceptionunifier.processor;

import cn.hutool.core.util.StrUtil;
import lombok.NoArgsConstructor;

import javax.annotation.processing.ProcessingEnvironment;

import static com.sxyangsuper.exceptionunifier.processor.Consts.PROCESSOR_ARG_NAME_REMOTE_BASE_URL;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ExceptionCodePrefixSupplierDetector {

    public static IExceptionCodePrefixSupplier detect(final ProcessingEnvironment processingEnv) {
        final String remoteBaseUrl = processingEnv.getOptions().get(PROCESSOR_ARG_NAME_REMOTE_BASE_URL);
        if (StrUtil.isNotBlank(remoteBaseUrl)) {
            return new RemoteExceptionCodePrefixSupplier(processingEnv);
        }
        return new ConfigurableExceptionCodePrefixSupplier(processingEnv);
    }
}
