package source.com.sxyangsuper.exceptionunifier.processor.ExceptionUnifierProcessorTest.should_not_change_code_of_non_exception_enum_without_implementing_exception_enum_asserts;

import com.sxyangsuper.exceptionunifier.processor.ExceptionSource;

import java.io.Serializable;

@ExceptionSource("A")
public enum SampleExceptionEnum implements Serializable {
    TEST("A", "");
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
