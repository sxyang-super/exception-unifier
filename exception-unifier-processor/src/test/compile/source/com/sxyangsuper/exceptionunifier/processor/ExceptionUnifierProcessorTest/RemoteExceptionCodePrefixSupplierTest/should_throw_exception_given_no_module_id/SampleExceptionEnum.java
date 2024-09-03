package source.com.sxyangsuper.exceptionunifier.processor.ExceptionUnifierProcessorTest.RemoteExceptionCodePrefixSupplierTest.should_throw_exception_given_no_module_id;

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
