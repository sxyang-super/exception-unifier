package source.com.sxyangsuper.exceptionunifier.processor.ExceptionUnifierProcessorTest.should_throw_exception_given_there_is_duplicate_code_in_different_enums;

import com.sxyangsuper.exceptionunifier.processor.ExceptionSource;

@ExceptionSource("A")
public enum SampleExceptionEnum implements ISampleExceptionEnumAsserts {
    TEST("A", ""),
    TEST2("B", "");
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
