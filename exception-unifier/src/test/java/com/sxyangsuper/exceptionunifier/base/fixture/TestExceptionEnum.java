package com.sxyangsuper.exceptionunifier.base.fixture;

import com.sxyangsuper.exceptionunifier.base.IExceptionEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TestExceptionEnum implements IExceptionEnum {
    ERROR_1("1", "error 1 {0}"),
    ERROR_2("2", "error 2 {0}");
    private final String code;
    private final String message;
}
