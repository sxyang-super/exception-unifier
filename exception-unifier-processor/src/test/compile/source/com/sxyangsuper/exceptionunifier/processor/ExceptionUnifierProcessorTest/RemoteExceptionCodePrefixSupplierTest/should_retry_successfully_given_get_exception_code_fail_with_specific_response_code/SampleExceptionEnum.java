package source.com.sxyangsuper.exceptionunifier.processor.ExceptionUnifierProcessorTest.RemoteExceptionCodePrefixSupplierTest.should_retry_successfully_given_get_exception_code_fail_with_specific_response_code;

import com.sxyangsuper.exceptionunifier.processor.ExceptionSource;

@ExceptionSource("A")
public enum SampleExceptionEnum implements ISampleExceptionEnumAsserts {
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
