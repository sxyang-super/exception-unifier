package io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.fixture.CheckHandlerTest.CheckExceptionEnumsTest.should_throw_exception_given_exception_enum_constructor_implementation_is_illegal;

import io.github.sxyangsuper.exceptionunifier.processor.ExceptionSource;
import lombok.Getter;

@Getter
@ExceptionSource("ABC")
public enum SampleExceptionEnum implements ISampleExceptionEnumAsserts {
    TEST("001", "Not found");
    private final String code;
    private final String message;

    SampleExceptionEnum(String code, String message) {
        this.code = "ABC";
        this.message = message;
    }
}
