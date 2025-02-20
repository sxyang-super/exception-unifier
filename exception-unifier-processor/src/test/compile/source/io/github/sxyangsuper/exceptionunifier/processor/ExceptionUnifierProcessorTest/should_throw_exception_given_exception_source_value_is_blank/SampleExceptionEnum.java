package source.io.github.sxyangsuper.exceptionunifier.processor.ExceptionUnifierProcessorTest.should_throw_exception_given_exception_source_value_is_blank;

import io.github.sxyangsuper.exceptionunifier.processor.ExceptionSource;

@ExceptionSource("  ")
public enum SampleExceptionEnum implements ISampleExceptionEnumAsserts {
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
