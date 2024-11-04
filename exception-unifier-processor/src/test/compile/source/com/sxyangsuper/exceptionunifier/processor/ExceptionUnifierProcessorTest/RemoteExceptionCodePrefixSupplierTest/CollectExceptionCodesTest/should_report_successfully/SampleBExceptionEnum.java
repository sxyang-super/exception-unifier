package source.com.sxyangsuper.exceptionunifier.processor.ExceptionUnifierProcessorTest.RemoteExceptionCodePrefixSupplierTest.CollectExceptionCodesTest.should_report_successfully;

import com.sxyangsuper.exceptionunifier.processor.ExceptionSource;

@ExceptionSource("B")
public enum SampleBExceptionEnum implements ISampleExceptionEnumAsserts {
    TEST("A", "This is test message {0}"),
    TEST_B("B", "This is test b message {0}");
    private final String code;
    private final String message;

    SampleBExceptionEnum(final String code, final String message) {
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
