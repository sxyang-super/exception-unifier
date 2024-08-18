package com.sxyangsuper.exceptionunifier.base.fixture;

import com.sxyangsuper.exceptionunifier.base.BaseException;
import com.sxyangsuper.exceptionunifier.base.IExceptionEnum;
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
