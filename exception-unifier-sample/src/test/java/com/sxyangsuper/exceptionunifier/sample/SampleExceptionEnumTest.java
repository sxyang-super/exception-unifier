package com.sxyangsuper.exceptionunifier.sample;

import org.junit.jupiter.api.Test;

import static com.sxyangsuper.exceptionunifier.sample.SampleExceptionEnum.TEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SampleExceptionEnumTest {

    @Test
    void should_get_correct_exception_code() {
        assertEquals("SAMPLE:U:001", TEST.getCode());
    }

    @Test
    void should_throw_exception_correctly() {
        final Object data = new Object();

        final SampleException sampleException = assertThrows(SampleException.class,
            () -> TEST.assertNotBlankWithData("  ", data, "Test")
        );

        assertEquals(TEST, sampleException.getExceptionEnum());
        assertEquals("Not found Test", sampleException.getMessage());
        assertNull(sampleException.getCause());
        final Object[] args = sampleException.getArgs();
        assertEquals(1, args.length);
        assertEquals("Test", args[0]);
        assertEquals(data, sampleException.getData());
    }
}
