package com.sxyangsuper.exceptionunifier.processor;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.Map;

import static com.sxyangsuper.exceptionunifier.processor.Consts.PROCESSOR_ARG_NAME_MODULE_ID;
import static com.sxyangsuper.exceptionunifier.processor.Consts.PROCESSOR_ARG_NAME_REMOTE_BASE_URL;
import static com.sxyangsuper.exceptionunifier.processor.Consts.REMOTE_EXCEPTION_CODE_PARAMETER_NAME_MODULE_ID;
import static com.sxyangsuper.exceptionunifier.processor.Consts.REMOTE_EXCEPTION_CODE_PATH_GET_PREFIX;

public class RemoteExceptionCodePrefixSupplier extends AbstractExceptionCodePrefixSupplier {

    public RemoteExceptionCodePrefixSupplier(final ProcessingEnvironment processingEnv) {
        super(processingEnv);
    }

    @Override
    public String get() {
        final Map<String, String> environmentOptions = this.processingEnv.getOptions();

        final String remoteBaseUrl = environmentOptions.get(PROCESSOR_ARG_NAME_REMOTE_BASE_URL);

        if (StrUtil.isBlank(remoteBaseUrl)) {
            throw new ExUnifierProcessException(
                String.format("Blank remote base url, please provide it with compile arg %s",
                    PROCESSOR_ARG_NAME_REMOTE_BASE_URL
                )
            );
        }

        final String moduleId = environmentOptions.get(PROCESSOR_ARG_NAME_MODULE_ID);

        if (StrUtil.isBlank(moduleId)) {
            throw new ExUnifierProcessException(
                String.format("Blank module id, please provide it with compile arg %s",
                    PROCESSOR_ARG_NAME_MODULE_ID
                )
            );
        }

        return getFromRemote(remoteBaseUrl, moduleId);
    }

    private String getFromRemote(final String remoteBaseUrl, final String moduleId) {
        final String exceptionCodePrefix;

        final String endpoint = String.format("%s?%s=%s",
            URLUtil.completeUrl(remoteBaseUrl, REMOTE_EXCEPTION_CODE_PATH_GET_PREFIX),
            REMOTE_EXCEPTION_CODE_PARAMETER_NAME_MODULE_ID, moduleId
        );

        try (HttpResponse response = HttpRequest.get(endpoint).execute()) {
            if (response.getStatus() != HttpStatus.HTTP_OK) {
                throw new ExUnifierProcessException(
                    String.format(
                        "Fail to get exception code prefix for module %s, response status is %s",
                        moduleId,
                        response.getStatus()
                    )
                );
            }

            exceptionCodePrefix = response.body();
        } catch (HttpException | IORuntimeException e) {
            throw new ExUnifierProcessException(
                String.format(
                    "Fail to get exception code prefix for module %s, remote request failed",
                    moduleId
                ),
                e
            );
        }

        return exceptionCodePrefix;
    }
}
