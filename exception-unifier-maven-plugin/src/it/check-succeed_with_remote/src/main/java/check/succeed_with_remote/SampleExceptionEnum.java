package check.succeed_with_remote;

import io.github.sxyangsuper.exceptionunifier.processor.ExceptionSource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ExceptionSource("ABC")
public enum SampleExceptionEnum implements ISampleExceptionEnumAsserts {
    TEST("001", "Not found"),
    TEST_02("002", "Not found 02"),
    TEST_03("003", "Not found 03");
    private final String code;
    private final String message;
}
