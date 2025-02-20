package io.github.sxyangsuper.exceptionunifier.mavenplugin.source;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.CharsetUtil;
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
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.MojoConfiguration;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.SyncResult;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.util.MojoConfigurationUtil;
import org.apache.maven.plugin.MojoExecutionException;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class RemoteExceptionCodeSource extends AbstractExceptionCodeSource {

    private static final String REMOTE_EXCEPTION_CODE_PATH_GET_PREFIX = "/prefix";
    private static final String REMOTE_EXCEPTION_CODE_PATH_REPORT_EXCEPTION_ENUMS = "/exception-enum/bulk";
    private static final List<Integer> RETRY_HTTP_RESPONSE_STATUS_CODES = Arrays.asList(
            HttpStatus.HTTP_INTERNAL_ERROR,
            HttpStatus.HTTP_BAD_GATEWAY,
            HttpStatus.HTTP_UNAVAILABLE,
            HttpStatus.HTTP_GATEWAY_TIMEOUT,
            HttpStatus.HTTP_TOO_MANY_REQUESTS
    );

    private MojoConfiguration mojoConfiguration;
    private String prefix;

    @Override
    public void setupAndCheck(final MojoConfiguration mojoConfiguration) throws MojoExecutionException {
        this.mojoConfiguration = mojoConfiguration;
        this.check();
    }

    @Override
    public String getExceptionCodePrefix() throws MojoExecutionException {
        if (this.prefix != null) {
            return this.prefix;
        }

        final String remoteBaseUrl = this.mojoConfiguration.getRemoteBaseURL();
        final String moduleId = MojoConfigurationUtil.getModuleId(this.mojoConfiguration);

        final String exceptionCodePrefixFromRemote = this.getExceptionCodePrefixFromRemote(remoteBaseUrl, moduleId);
        super.validateExceptionCodePrefix(exceptionCodePrefixFromRemote);
        return this.prefix = exceptionCodePrefixFromRemote;
    }

    private String getExceptionCodePrefixFromRemote(final String remoteBaseUrl, final String moduleId) throws MojoExecutionException {
        final String exceptionCodePrefix;

        final String endpoint = String.format("%s/%s",
                URLUtil.completeUrl(remoteBaseUrl, REMOTE_EXCEPTION_CODE_PATH_GET_PREFIX),
                moduleId
        );

        final RetryConfig config = RetryConfig.<HttpResponse>custom()
                .maxAttempts(4)
                .waitDuration(Duration.ofSeconds(1))
                .retryOnResult(response -> RETRY_HTTP_RESPONSE_STATUS_CODES.contains(response.getStatus()))
                .retryExceptions(HttpException.class, IORuntimeException.class)
                .failAfterMaxAttempts(true)
                .build();

        final Retry getExceptionCodeRetry = RetryRegistry.of(config)
                .retry("get exception code retry");

        try (
                HttpResponse response = Decorators.ofSupplier(() -> HttpRequest.get(constructEndpointWithQuery(endpoint))
                                .execute())
                        .withRetry(getExceptionCodeRetry)
                        .get()
        ) {
            if (response.getStatus() != HttpStatus.HTTP_OK) {
                throw new MojoExecutionException(
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
            throw new MojoExecutionException(
                    String.format(
                            "Fail to get exception code prefix for module %s, remote request failed",
                            moduleId
                    ),
                    e
            );
        }

        return exceptionCodePrefix;
    }

    private String constructEndpointWithQuery(final String endpoint) {
        final String query = URLUtil.buildQuery(this.mojoConfiguration.getRemoteQuery(), CharsetUtil.CHARSET_UTF_8);
        return StrUtil.isBlank(query) ? endpoint : String.format("%s?%s", endpoint, query);
    }


    private void check() throws MojoExecutionException {
        if (StrUtil.isBlank(this.mojoConfiguration.getRemoteBaseURL())) {
            throw new MojoExecutionException("remoteBaseURL can not be blank");
        }
    }

    @Override
    public void push(final SyncResult syncResult) throws MojoExecutionException {
        final String url = this.constructEndpointWithQuery(
                URLUtil.completeUrl(this.mojoConfiguration.getRemoteBaseURL(), REMOTE_EXCEPTION_CODE_PATH_REPORT_EXCEPTION_ENUMS)
        );

        try (HttpResponse response = HttpRequest.post(url)
                .body(JSONUtil.toJsonStr(syncResult), ContentType.JSON.getValue())
                .execute()) {

            if (response.getStatus() != HttpStatus.HTTP_CREATED) {
                throw new MojoExecutionException(
                        String.format(
                                "Fail to report exception enums, response status is %s, message is %s",
                                response.getStatus(),
                                response.body()
                        )
                );
            }
        } catch (HttpException | IORuntimeException e) {
            throw new MojoExecutionException(
                    "Fail to push sync result to server",
                    e
            );
        }
    }
}
