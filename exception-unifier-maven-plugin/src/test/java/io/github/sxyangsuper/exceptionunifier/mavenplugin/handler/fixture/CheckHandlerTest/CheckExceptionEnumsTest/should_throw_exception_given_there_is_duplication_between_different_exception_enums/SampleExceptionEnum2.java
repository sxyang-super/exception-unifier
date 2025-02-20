package io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.fixture.CheckHandlerTest.CheckExceptionEnumsTest.should_throw_exception_given_there_is_duplication_between_different_exception_enums;

import io.github.sxyangsuper.exceptionunifier.processor.ExceptionSource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ExceptionSource("ABC")
public enum SampleExceptionEnum2 implements ISampleExceptionEnumAsserts {
    TEST("001", "Not found");
    private final String code;
    private final String message;
}
