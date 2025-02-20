package io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.fixture.CheckHandlerTest.CheckExceptionEnumsTest.should_throw_exception_given_exception_enum_message_value_is_not_set_by_constructor;

import io.github.sxyangsuper.exceptionunifier.processor.ExceptionSource;
import lombok.Getter;

@Getter
@ExceptionSource("ABC")
public enum SampleExceptionEnum implements ISampleExceptionEnumAsserts {
    TEST("Not found");
    private final String code;
    private String message;

    SampleExceptionEnum(String code) {
        this.code = code;
    }

    public String getMessage() {
        return "Test";
    }
}
