package com.sxyangsuper.exceptionunifier.processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.annotation.processing.ProcessingEnvironment;

import static org.junit.jupiter.api.Assertions.assertNull;

class ConfigurableExceptionCodePrefixSupplierTest {
    ConfigurableExceptionCodePrefixSupplier configurableExceptionCodePrefixSupplier;
    ProcessingEnvironment processingEnvironment;

    @BeforeEach
    void setUp() {
        processingEnvironment = Mockito.mock(ProcessingEnvironment.class);
        configurableExceptionCodePrefixSupplier = new ConfigurableExceptionCodePrefixSupplier(processingEnvironment, Mockito.mock(Logger.class));
    }


    @Test
    void should_get_null_module_id() {
        assertNull(configurableExceptionCodePrefixSupplier.getModuleId());
    }
}
