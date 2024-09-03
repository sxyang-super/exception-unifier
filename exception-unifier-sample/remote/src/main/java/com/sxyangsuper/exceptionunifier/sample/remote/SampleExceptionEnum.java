package com.sxyangsuper.exceptionunifier.sample.remote;

import com.sxyangsuper.exceptionunifier.processor.ExceptionSource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ExceptionSource("U")
public enum SampleExceptionEnum implements ISampleExceptionEnumAsserts {
    TEST("001", "Not found {0}");
    private final String code;
    private final String message;
}
