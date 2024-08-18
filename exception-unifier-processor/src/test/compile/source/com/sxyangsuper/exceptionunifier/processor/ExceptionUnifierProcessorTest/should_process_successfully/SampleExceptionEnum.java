package source.com.sxyangsuper.exceptionunifier.processor.ExceptionUnifierProcessorTest.should_process_successfully;

import com.sxyangsuper.exceptionunifier.processor.ExceptionSource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
