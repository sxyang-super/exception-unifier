package com.sxyangsuper.exceptionunifier.processor;

import cn.hutool.core.map.MapUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.annotation.processing.ProcessingEnvironment;

import static com.sxyangsuper.exceptionunifier.processor.Consts.PROCESSOR_ARG_NAME_REMOTE_BASE_URL;
import static org.junit.jupiter.api.Assertions.*;

class RemoteExceptionCodePrefixSupplierTest {

    RemoteExceptionCodePrefixSupplier remoteExceptionCodePrefixSupplier;
    ProcessingEnvironment processingEnvironment;

    @BeforeEach
    void setUp() {
        processingEnvironment = Mockito.mock(ProcessingEnvironment.class);
        remoteExceptionCodePrefixSupplier = new RemoteExceptionCodePrefixSupplier(processingEnvironment);
    }

    @Test
    void should_throw_exception_given_base_url_is_blank() {
        Mockito.when(processingEnvironment.getOptions()).thenReturn(MapUtil.empty());

        final ExUnifierProcessException exception = assertThrows(ExUnifierProcessException.class, () -> remoteExceptionCodePrefixSupplier.get());

        Assertions.assertEquals(
            String.format("Blank remote base url, please provide it with compile arg %s", PROCESSOR_ARG_NAME_REMOTE_BASE_URL),
            exception.getMessage()
        );

    }
}
