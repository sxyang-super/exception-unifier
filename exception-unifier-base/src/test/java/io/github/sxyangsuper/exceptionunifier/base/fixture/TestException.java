package io.github.sxyangsuper.exceptionunifier.base.fixture;

import io.github.sxyangsuper.exceptionunifier.base.BaseException;
import io.github.sxyangsuper.exceptionunifier.base.IExceptionEnum;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class TestException extends BaseException {

    public static TestException of(
        final IExceptionEnum responseEnum,
        final Object... args
    ) {
        return of(TestException::new, responseEnum, args);
    }
}
