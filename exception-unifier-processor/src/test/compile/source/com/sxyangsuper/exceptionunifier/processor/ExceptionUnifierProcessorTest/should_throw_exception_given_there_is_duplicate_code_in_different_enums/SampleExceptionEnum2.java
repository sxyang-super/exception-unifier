package source.com.sxyangsuper.exceptionunifier.processor.ExceptionUnifierProcessorTest.should_throw_exception_given_there_is_duplicate_code_in_different_enums;

import com.sxyangsuper.exceptionunifier.processor.ExceptionSource;

@ExceptionSource("A")
public enum SampleExceptionEnum2 implements ISampleExceptionEnumAsserts {
    TEST("B", "");
    private final String code;
    private final String message;

    SampleExceptionEnum2(final String code, final String message) {
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
