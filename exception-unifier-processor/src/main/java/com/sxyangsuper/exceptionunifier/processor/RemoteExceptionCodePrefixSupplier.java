package com.sxyangsuper.exceptionunifier.processor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.retry.MaxRetriesExceededException;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.ProcessingEnvironment;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.sxyangsuper.exceptionunifier.base.Consts.EXCEPTION_CODE_SPLITTER;
import static com.sxyangsuper.exceptionunifier.processor.Consts.PROCESSOR_ARG_NAME_MODULE_ID;
import static com.sxyangsuper.exceptionunifier.processor.Consts.PROCESSOR_ARG_NAME_REMOTE_BASE_URL;
import static com.sxyangsuper.exceptionunifier.processor.Consts.REMOTE_EXCEPTION_CODE_PARAMETER_NAME_MODULE_ID;
import static com.sxyangsuper.exceptionunifier.processor.Consts.REMOTE_EXCEPTION_CODE_PATH_GET_PREFIX;
import static com.sxyangsuper.exceptionunifier.processor.Consts.REMOTE_EXCEPTION_CODE_PATH_REPORT_EXCEPTION_ENUMS;

/**
 * using endpoints:
 * 1. {@link Consts#REMOTE_EXCEPTION_CODE_PATH_GET_PREFIX } is supposed to return {@link HttpStatus#HTTP_OK }
 * 2. {@link Consts#REMOTE_EXCEPTION_CODE_PATH_REPORT_EXCEPTION_ENUMS } is supposed to return {@link HttpStatus#HTTP_CREATED }
 */
public class RemoteExceptionCodePrefixSupplier extends AbstractExceptionCodePrefixSupplier {
    private final List<Integer> retryHttpResponseStatusCodes = Arrays.asList(
        HttpStatus.HTTP_INTERNAL_ERROR,
        HttpStatus.HTTP_BAD_GATEWAY,
        HttpStatus.HTTP_UNAVAILABLE,
        HttpStatus.HTTP_GATEWAY_TIMEOUT,
        HttpStatus.HTTP_TOO_MANY_REQUESTS
    );

    private String moduleId;
    private String prefix;

    public RemoteExceptionCodePrefixSupplier(final ProcessingEnvironment processingEnv, final Logger logger) {
        super(processingEnv, logger);
    }

    @Override
    public String get() {
        if (this.prefix != null) {
            return this.prefix;
        }

        final String remoteBaseUrl = getRemoteBaseUrl();
        final String moduleId = getModuleId();

        return this.prefix = getFromRemote(remoteBaseUrl, moduleId);
    }

    @Override
    public String getModuleId() {
        if (this.moduleId != null) {
            return this.moduleId;
        }

        final Map<String, String> environmentOptions = this.processingEnv.getOptions();
        final String moduleId = environmentOptions.get(PROCESSOR_ARG_NAME_MODULE_ID);

        if (StrUtil.isBlank(moduleId)) {
            throw new ExUnifierProcessException(
                String.format("Blank module id, please provide it with compile arg %s",
                    PROCESSOR_ARG_NAME_MODULE_ID
                )
            );
        }

        return this.moduleId = moduleId;
    }

    @NotNull
    private String getRemoteBaseUrl() {
        final Map<String, String> environmentOptions = this.processingEnv.getOptions();

        final String remoteBaseUrl = environmentOptions.get(PROCESSOR_ARG_NAME_REMOTE_BASE_URL);

        if (StrUtil.isBlank(remoteBaseUrl)) {
            throw new ExUnifierProcessException(
                String.format("Blank remote base url, please provide it with compile arg %s",
                    PROCESSOR_ARG_NAME_REMOTE_BASE_URL
                )
            );
        }
        return remoteBaseUrl;
    }

    private String getFromRemote(final String remoteBaseUrl, final String moduleId) {
        final String exceptionCodePrefix;

        final String endpoint = String.format("%s?%s=%s",
            URLUtil.completeUrl(remoteBaseUrl, REMOTE_EXCEPTION_CODE_PATH_GET_PREFIX),
            REMOTE_EXCEPTION_CODE_PARAMETER_NAME_MODULE_ID, moduleId
        );

        final RetryConfig config = RetryConfig.<HttpResponse>custom()
            .maxAttempts(3)
            .waitDuration(Duration.ofSeconds(1))
            .retryOnResult(response -> retryHttpResponseStatusCodes.contains(response.getStatus()))
            .retryExceptions(HttpException.class, IORuntimeException.class)
            .failAfterMaxAttempts(true)
            .build();

        final Retry getExceptionCodeRetry = RetryRegistry.of(config)
            .retry("get exception code retry");

        try (
            HttpResponse response = Decorators.ofSupplier(() -> HttpRequest.get(endpoint).execute())
                .withRetry(getExceptionCodeRetry)
                .get()
        ) {
            if (response.getStatus() != HttpStatus.HTTP_OK) {
                throw new ExUnifierProcessException(
                    String.format(
                        "Fail to get exception code prefix for module %s, response status is %s, message is %s",
                        moduleId,
                        response.getStatus(),
                        response.body()
                    )
                );
            }
            exceptionCodePrefix = response.body();
        } catch (MaxRetriesExceededException | IORuntimeException e) {
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

    @Override
    public void collectExceptionCodes(final List<ExceptionCodeExpressions> exceptionCodeExpressionsList) {
        if (CollUtil.isEmpty(exceptionCodeExpressionsList)) {
            return;
        }

        final ExceptionCodeReportMeta exceptionCodeReportMeta = getExceptionCodeReportMeta(exceptionCodeExpressionsList);

        reportToRemoteServer(exceptionCodeExpressionsList, exceptionCodeReportMeta);
    }

    private void reportToRemoteServer(final List<ExceptionCodeExpressions> exceptionCodeExpressionsList, final ExceptionCodeReportMeta exceptionCodeReportMeta) {
        final String remoteBaseUrl = this.getRemoteBaseUrl();

        final String endpoint = URLUtil.completeUrl(remoteBaseUrl, REMOTE_EXCEPTION_CODE_PATH_REPORT_EXCEPTION_ENUMS);

        try (HttpResponse response = HttpRequest.post(endpoint)
            .body(JSONUtil.toJsonStr(exceptionCodeReportMeta), ContentType.JSON.getValue())
            .execute()) {

            if (response.getStatus() == HttpStatus.HTTP_NOT_FOUND) {
                this.logger.warn(String.format(
                    "Not found endpoint %s to report exception enums",
                    endpoint
                ));
                return;
            }

            if (response.getStatus() != HttpStatus.HTTP_CREATED) {
                throw new ExUnifierProcessException(
                    String.format(
                        "Fail to report exception enums, response status is %s, message is %s",
                        response.getStatus(),
                        response.body()
                    )
                );
            }

            this.logger.note(String.format(
                "Successfully report %s exception enums to server",
                exceptionCodeExpressionsList.size()
            ));
        } catch (HttpException | IORuntimeException e) {
            throw new ExUnifierProcessException(
                "Fail to report exception codes to server",
                e
            );
        }
    }

    @NotNull
    private ExceptionCodeReportMeta getExceptionCodeReportMeta(final List<ExceptionCodeExpressions> exceptionCodeExpressionsList) {
        final ExceptionCodeReportMeta exceptionCodeReportMeta = new ExceptionCodeReportMeta()
            .setModuleId(this.getModuleId())
            .setPrefix(this.get());

        final List<ExceptionCodeReportMeta.MetaSource> metaMetaSources = exceptionCodeExpressionsList
            .stream()
            .collect(Collectors.groupingBy(exceptionCodeExpressions -> {
                final String code = exceptionCodeExpressions.getCodeExpressionValue();
                return code.split(EXCEPTION_CODE_SPLITTER)[1];
            }))
            .entrySet()
            .stream()
            .map(sourceToExceptionCodeExpressionsList -> {
                final ExceptionCodeReportMeta.MetaSource metaSource = new ExceptionCodeReportMeta.MetaSource()
                    .setSource(sourceToExceptionCodeExpressionsList.getKey());

                final List<ExceptionCodeReportMeta.ExceptionCode> metaExceptionCodes = sourceToExceptionCodeExpressionsList.getValue()
                    .stream()
                    .map(sourceToExceptionCodeExpressions ->
                        new ExceptionCodeReportMeta.ExceptionCode()
                            .setCode(sourceToExceptionCodeExpressions.getCodeExpressionValue())
                            .setMessagePlaceholder(sourceToExceptionCodeExpressions.getMessageExpressionValue())
                    )
                    .collect(Collectors.toList());

                metaSource.setExceptionCodes(metaExceptionCodes);
                return metaSource;
            })
            .collect(Collectors.toList());

        exceptionCodeReportMeta.setMetaSources(metaMetaSources);
        return exceptionCodeReportMeta;
    }
}
