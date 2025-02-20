package io.github.sxyangsuper.exceptionunifier.base;

public interface IExceptionEnum {
    String getCode();

    String getMessage();

    default String getExceptionSource() {
        return this.getCode().split(Consts.EXCEPTION_CODE_SPLITTER)[1];
    }
}
