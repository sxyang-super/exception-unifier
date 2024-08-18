package com.sxyangsuper.exceptionunifier.base;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.function.Supplier;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
public class BaseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    protected IExceptionEnum exceptionEnum;
    protected String message;
    protected Object data;
    protected Object[] args;

    public static <T extends BaseException> T of(
        final Supplier<T> exceptionSupplier,
        final IExceptionEnum responseEnum,
        final Object... args
    ) {
        final T baseException = exceptionSupplier.get();
        baseException.exceptionEnum = responseEnum;
        baseException.message = MessageFormat.format(responseEnum.getMessage(), args);
        baseException.args = args;
        return baseException;
    }

    public static <T extends BaseException> T ofWithCause(
        final Supplier<T> exceptionSupplier,
        final IExceptionEnum responseEnum,
        final Throwable cause,
        final Object... args
    ) {
        final T baseException = of(exceptionSupplier, responseEnum, args);
        baseException.initCause(cause);
        return baseException;
    }

    public static <T extends BaseException> T ofWithData(
        final Supplier<T> exceptionSupplier,
        final IExceptionEnum responseEnum,
        final Object data,
        final Object... args
    ) {
        final T baseException = of(exceptionSupplier, responseEnum, args);
        baseException.data = data;
        return baseException;
    }

    public static <T extends BaseException> T ofWithCauseAndData(
        final Supplier<T> exceptionSupplier,
        final IExceptionEnum responseEnum,
        final Throwable cause,
        final Object data,
        final Object... args
    ) {
        final T baseException = of(exceptionSupplier, responseEnum, args);
        baseException.initCause(cause);
        baseException.data = data;
        return baseException;
    }

    @Override
    public boolean equals(final Object target) {
        if (this == target) {
            return true;
        }
        if (target == null || getClass() != target.getClass()) {
            return false;
        }
        final BaseException targetException = (BaseException) target;
        return Objects.equals(exceptionEnum.getCode(), targetException.exceptionEnum.getCode())
            && Objects.equals(message, targetException.message)
            && Objects.equals(data, targetException.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Objects.hash(exceptionEnum.getCode()), data, message);
    }
}
