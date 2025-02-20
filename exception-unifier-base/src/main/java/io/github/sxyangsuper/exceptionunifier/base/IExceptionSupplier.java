package io.github.sxyangsuper.exceptionunifier.base;

import java.util.function.Supplier;

public interface IExceptionSupplier<E extends BaseException> {
    E newE(Object... args);

    E newEWithCause(Throwable cause, Object... args);

    E newEWithData(Object data, Object... args);

    E newEWithCauseAndData(Throwable cause, Object data, Object... args);

    default Supplier<BaseException> newESupplier(final Object... args) {
        return () -> newE(args);
    }

    default Supplier<BaseException> newEWithCauseSupplier(final Throwable cause, final Object... args) {
        return () -> newEWithCause(cause, args);
    }

    default Supplier<BaseException> newEWithDataSupplier(final Object data, final Object... args) {
        return () -> newEWithData(data, args);
    }

    default Supplier<BaseException> newEWithCauseAndDataSupplier(
        final Throwable cause,
        final Object data,
        final Object... args
    ) {
        return () -> newEWithCauseAndData(cause, data, args);
    }
}
