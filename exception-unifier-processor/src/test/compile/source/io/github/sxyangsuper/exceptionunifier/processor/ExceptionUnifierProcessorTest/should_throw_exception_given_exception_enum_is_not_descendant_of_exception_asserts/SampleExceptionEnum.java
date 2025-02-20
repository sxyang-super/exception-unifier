package source.io.github.sxyangsuper.exceptionunifier.processor.ExceptionUnifierProcessorTest.should_throw_exception_given_exception_enum_is_not_descendant_of_exception_asserts;

import io.github.sxyangsuper.exceptionunifier.processor.ExceptionSource;

import java.io.Serializable;

@ExceptionSource("ABC")
public enum SampleExceptionEnum implements Serializable {
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
