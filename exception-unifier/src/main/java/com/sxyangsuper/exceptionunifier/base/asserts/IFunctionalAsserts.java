package com.sxyangsuper.exceptionunifier.base.asserts;

import com.sxyangsuper.exceptionunifier.base.BaseException;
import com.sxyangsuper.exceptionunifier.base.Executable;
import com.sxyangsuper.exceptionunifier.base.IExceptionSupplier;
import org.jetbrains.annotations.Contract;

import java.util.function.Supplier;

public interface IFunctionalAsserts<E extends BaseException> extends IExceptionSupplier<E> {
    // assert supply successfully

    @Contract("null,_->fail")
    default <T> T assertSupplySuccessfully(final Supplier<T> supplier, final Object... args) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw newEWithCause(e, args);
        }
    }

    @Contract("null,_,_->fail")
    default <T> T assertSupplySuccessfullyWithData(
        final Supplier<T> supplier,
        final Object data,
        final Object... args
    ) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw newEWithCauseAndData(e, data, args);
        }
    }

    @Contract("null,_,_->fail")
    default <T> T assertSupplySuccessfullyWithDataSupplier(
        final Supplier<T> supplier,
        final Supplier<Object> dataSupplier,
        final Object... args
    ) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw newEWithCauseAndData(e, dataSupplier.get(), args);
        }
    }

    // assert execute successfully

    default void assertExecuteSuccessfully(final Executable executable, final Object... args) {
        try {
            executable.execute();
        } catch (Exception e) {
            throw newEWithCause(e, args);
        }
    }

    default void assertExecuteSuccessfullyWithData(
        final Executable executable,
        final Object data,
        final Object... args
    ) {
        try {
            executable.execute();
        } catch (Exception e) {
            throw newEWithCauseAndData(e, data, args);
        }
    }

    default void assertExecuteSuccessfullyWithDataSupplier(
        final Executable executable,
        final Supplier<Object> dataSupplier,
        final Object... args
    ) {
        try {
            executable.execute();
        } catch (Exception e) {
            throw newEWithCauseAndData(e, dataSupplier.get(), args);
        }
    }
}
