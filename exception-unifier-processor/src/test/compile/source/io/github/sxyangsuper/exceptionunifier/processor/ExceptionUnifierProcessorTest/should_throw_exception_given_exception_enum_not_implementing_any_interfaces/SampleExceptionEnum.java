package source.io.github.sxyangsuper.exceptionunifier.processor.ExceptionUnifierProcessorTest.should_throw_exception_given_exception_enum_not_implementing_any_interfaces;

import io.github.sxyangsuper.exceptionunifier.processor.ExceptionSource;

@ExceptionSource("ABC")
public enum SampleExceptionEnum {
    TEST("abc", "DEF");
    private final String code;
    private final String message;

    SampleExceptionEnum(final String code, final String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
