package source.com.sxyangsuper.exceptionunifier.processor.ExceptionUnifierProcessorTest.should_throw_exception_given_exception_enum_does_not_have_exception_source_annotation;

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
