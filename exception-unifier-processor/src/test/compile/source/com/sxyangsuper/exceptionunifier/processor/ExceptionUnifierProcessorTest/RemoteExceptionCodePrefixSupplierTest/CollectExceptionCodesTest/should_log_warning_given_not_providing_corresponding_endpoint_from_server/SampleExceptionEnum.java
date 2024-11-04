package source.com.sxyangsuper.exceptionunifier.processor.ExceptionUnifierProcessorTest.RemoteExceptionCodePrefixSupplierTest.CollectExceptionCodesTest.should_log_warning_given_not_providing_corresponding_endpoint_from_server;

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
