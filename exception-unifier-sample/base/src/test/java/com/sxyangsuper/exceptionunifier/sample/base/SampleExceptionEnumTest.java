package com.sxyangsuper.exceptionunifier.sample.base;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SampleExceptionEnumTest {

    @Test
    void should_get_correct_exception_code() {
        assertEquals("SAMPLE:U:001", SampleExceptionEnum.TEST.getCode());
    }

    @Test
    void should_throw_exception_correctly() {
        final Object data = new Object();

        final SampleException sampleException = assertThrows(SampleException.class,
            () -> SampleExceptionEnum.TEST.assertNotBlankWithData("  ", data, "Test")
        );

        assertEquals(SampleExceptionEnum.TEST, sampleException.getExceptionEnum());
        assertEquals("Not found Test", sampleException.getMessage());
        assertNull(sampleException.getCause());
        final Object[] args = sampleException.getArgs();
        assertEquals(1, args.length);
        assertEquals("Test", args[0]);
        assertEquals(data, sampleException.getData());
    }
}
