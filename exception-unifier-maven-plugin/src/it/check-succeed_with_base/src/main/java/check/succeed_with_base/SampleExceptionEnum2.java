package check.succeed_with_base;

import io.github.sxyangsuper.exceptionunifier.processor.ExceptionSource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ExceptionSource("ABC")
public enum SampleExceptionEnum2 implements ISampleExceptionEnumAsserts {
    TEST("004", "Not found 04"),
    TEST_02("005", "Not found 05"),
    TEST_03("006", "Not found 06");
    private final String code;
    private final String message;
}
