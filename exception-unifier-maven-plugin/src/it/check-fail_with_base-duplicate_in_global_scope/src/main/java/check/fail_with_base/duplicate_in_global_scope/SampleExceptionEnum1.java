package check.fail_with_base.duplicate_in_global_scope;

import io.github.sxyangsuper.exceptionunifier.processor.ExceptionSource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ExceptionSource("ABC")
public enum SampleExceptionEnum1 implements ISampleExceptionEnumAsserts {
    TEST("001", "Not found");
    private final String code;
    private final String message;
}
