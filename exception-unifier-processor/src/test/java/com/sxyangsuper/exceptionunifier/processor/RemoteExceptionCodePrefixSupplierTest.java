package com.sxyangsuper.exceptionunifier.processor;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.Collections;
import java.util.List;

import static com.sxyangsuper.exceptionunifier.processor.Consts.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class RemoteExceptionCodePrefixSupplierTest {

    RemoteExceptionCodePrefixSupplier remoteExceptionCodePrefixSupplier;
    ProcessingEnvironment processingEnvironment;

    @BeforeEach
    void setUp() {
        processingEnvironment = Mockito.mock(ProcessingEnvironment.class);
        remoteExceptionCodePrefixSupplier = new RemoteExceptionCodePrefixSupplier(processingEnvironment, Mockito.mock(Logger.class));
    }

    @Test
    void should_throw_exception_given_base_url_is_blank() {
        when(processingEnvironment.getOptions()).thenReturn(MapUtil.empty());

        final ExUnifierProcessException exception = assertThrows(ExUnifierProcessException.class, () -> remoteExceptionCodePrefixSupplier.get());

        Assertions.assertEquals(
            String.format("Blank remote base url, please provide it with compile arg %s", PROCESSOR_ARG_NAME_REMOTE_BASE_URL),
            exception.getMessage()
        );
    }

    @Nested
    class CollectExceptionCodesTest {

        @Test
        void should_succeed_given_exception_code_expressions_list_is_null() {
            assertDoesNotThrow(() -> remoteExceptionCodePrefixSupplier.collectExceptionCodes(null));
        }

        @Test
        void should_throw_exception_given_communicate_with_exception_code_server_fail() {
            ExceptionCodeExpressions exceptionCodeExpressions = Mockito.mock(ExceptionCodeExpressions.class);
            final List<ExceptionCodeExpressions> exceptionCodeExpressionsList = Collections.singletonList(
                exceptionCodeExpressions
            );

            String remoteBaseUrl = "http://localhost";
            String moduleId = "moduleId";
            when(processingEnvironment.getOptions()).thenReturn(
                MapUtil
                    .<String, String>builder()
                    .put(PROCESSOR_ARG_NAME_REMOTE_BASE_URL, remoteBaseUrl)
                    .put(PROCESSOR_ARG_NAME_MODULE_ID, moduleId)
                    .build()
            );

            final String getPrefixEndpoint = String.format("%s?%s=%s",
                URLUtil.completeUrl(remoteBaseUrl, REMOTE_EXCEPTION_CODE_PATH_GET_PREFIX),
                REMOTE_EXCEPTION_CODE_PARAMETER_NAME_MODULE_ID, moduleId
            );

            HttpRequest httpRequest = Mockito.mock(HttpRequest.class);
            MockedStatic<HttpRequest> httpRequestMockedStatic = mockStatic(HttpRequest.class);
            httpRequestMockedStatic.when(() -> HttpRequest.get(getPrefixEndpoint)).thenReturn(httpRequest);

            HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
            when(httpRequest.execute()).thenReturn(httpResponse);

            when(httpResponse.getStatus()).thenReturn(HttpStatus.HTTP_OK);
            when(httpResponse.body()).thenReturn("exceptionCodePrefix");

            when(exceptionCodeExpressions.getCodeExpressionValue()).thenReturn("A:B:C");
            when(exceptionCodeExpressions.getMessageExpressionValue()).thenReturn("messageExpressionValue");

            final String reportExceptionEnumsEndpoint = URLUtil.completeUrl(remoteBaseUrl, REMOTE_EXCEPTION_CODE_PATH_REPORT_EXCEPTION_ENUMS);

            IORuntimeException exceptionWhileReportExceptionEnums = new IORuntimeException("exception while report exception enums");
            httpRequestMockedStatic.when(() -> HttpRequest.post(reportExceptionEnumsEndpoint)).thenThrow(exceptionWhileReportExceptionEnums);

            ExUnifierProcessException exUnifierProcessException = assertThrows(ExUnifierProcessException.class, () -> remoteExceptionCodePrefixSupplier.collectExceptionCodes(exceptionCodeExpressionsList));

            assertEquals("Fail to report exception codes to server", exUnifierProcessException.getMessage());
            assertEquals(exceptionWhileReportExceptionEnums, exUnifierProcessException.getCause());
        }
    }
}
