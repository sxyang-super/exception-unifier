package com.sxyangsuper.exceptionunifier.base.asserts;

import com.sxyangsuper.exceptionunifier.base.BaseException;
import com.sxyangsuper.exceptionunifier.base.IExceptionSupplier;
import org.jetbrains.annotations.Contract;

import java.util.Optional;
import java.util.function.Supplier;

public interface IBaseAsserts<E extends BaseException> extends IExceptionSupplier<E> {

    @Contract("null,_->fail; !null,_->!null")
    default <T> T assertNotNull(final T target, final Object... args) {
        return Optional.ofNullable(target).orElseThrow(() -> newE(args));
    }

    @Contract("null,_,_->fail; !null,_,_->!null")
    default <T> T assertNotNullWithData(
        final T target,
        final Object data,
        final Object... args
    ) {
        return Optional.ofNullable(target).orElseThrow(() -> newEWithData(data, args));
    }

    @Contract("null,_,_->fail; !null,_,_->!null")
    default <T> T assertNotNullWithDataSupplier(
        final T target,
        final Supplier<Object> dataSupplier,
        final Object... args
    ) {
        return Optional.ofNullable(target).orElseThrow(() -> newEWithData(dataSupplier.get(), args));
    }

    // assert null

    @Contract("!null,_->fail")
    default void assertNull(final Object target, final Object... args) {
        Optional.ofNullable(target).ifPresent(nonNullTarget -> {
            throw newE(args);
        });
    }

    @Contract("!null,_,_->fail")
    default void assertNullWithData(
        final Object target,
        final Object data,
        final Object... args
    ) {
        Optional.ofNullable(target).ifPresent(nonNullTarget -> {
            throw newEWithData(data, args);
        });
    }

    // assert true

    @Contract("false,_->fail")
    default void assertTrue(final Boolean target, final Object... args) {
        if (!Boolean.TRUE.equals(target)) {
            throw newE(args);
        }
    }

    @Contract("false,_,_->fail")
    default void assertTrueWithData(
        final Boolean target,
        final Object data,
        final Object... args
    ) {
        if (!Boolean.TRUE.equals(target)) {
            throw newEWithData(data, args);
        }
    }

    // assert false

    @Contract("true,_->fail")
    default void assertFalse(final Boolean target, final Object... args) {
        if (!Boolean.FALSE.equals(target)) {
            throw newE(args);
        }
    }

    @Contract("true,_,_->fail")
    default void assertFalseWithData(
        final Boolean target,
        final Object data,
        final Object... args
    ) {
        if (!Boolean.FALSE.equals(target)) {
            throw newEWithData(data, args);
        }
    }
}
