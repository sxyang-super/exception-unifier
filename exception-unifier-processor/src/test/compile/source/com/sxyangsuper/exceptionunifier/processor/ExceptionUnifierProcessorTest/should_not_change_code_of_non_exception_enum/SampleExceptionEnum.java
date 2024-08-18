package source.com.sxyangsuper.exceptionunifier.processor.ExceptionUnifierProcessorTest.should_not_change_code_of_non_exception_enum;

import com.sxyangsuper.exceptionunifier.processor.ExceptionSource;

@ExceptionSource("A")
public enum SampleExceptionEnum {
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
