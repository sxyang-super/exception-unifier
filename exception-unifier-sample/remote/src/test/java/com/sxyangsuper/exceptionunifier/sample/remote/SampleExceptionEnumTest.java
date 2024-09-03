package com.sxyangsuper.exceptionunifier.sample.remote;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SampleExceptionEnumTest {

    @Test
    void should_get_correct_exception_code() {
        assertEquals("SAMPLE-REMOTE:U:001", SampleExceptionEnum.TEST.getCode());
    }
}
