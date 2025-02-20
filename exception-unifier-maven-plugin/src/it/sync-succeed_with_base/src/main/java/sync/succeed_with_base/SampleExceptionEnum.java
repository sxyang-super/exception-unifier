package sync.succeed_with_base;

import io.github.sxyangsuper.exceptionunifier.processor.ExceptionSource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ExceptionSource("ABC")
public enum SampleExceptionEnum implements ISampleExceptionEnumAsserts {
    TEST("001", "Not found");
    private final String code;
    private final String message;
}
