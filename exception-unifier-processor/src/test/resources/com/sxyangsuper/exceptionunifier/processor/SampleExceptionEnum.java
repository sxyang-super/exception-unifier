package com.sxyangsuper.exceptionunifier.sample;

import com.sxyangsuper.exceptionunifier.processor.ExceptionSource;
import lombok.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@ExceptionSource("G")
public enum SampleExceptionEnum implements ISampleExceptionEnumAsserts {
    TEST("abc", "DEF"),
    TEST1("defGG", "DEF");

    private final String code;
    private final String message;

    @Generated
    public String getCode() {
        return this.code;
    }

    @Generated
    public String getMessage() {
        return this.message;
    }

    @Generated
    private SampleExceptionEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
