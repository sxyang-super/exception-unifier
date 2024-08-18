package com.sxyangsuper.exceptionunifier.base.asserts;

import cn.hutool.core.collection.CollectionUtil;
import com.sxyangsuper.exceptionunifier.base.BaseException;
import com.sxyangsuper.exceptionunifier.base.IExceptionSupplier;
import org.jetbrains.annotations.Contract;

import java.util.function.Supplier;

public interface IIterableAsserts<E extends BaseException> extends IExceptionSupplier<E> {
    // assert not empty

    @Contract("null,_->fail;!null,_->!null")
    default <T extends Iterable<?>> T assertNotEmpty(final Iterable<?> target, final Object... args) {
        if (CollectionUtil.isEmpty(target)) {
            throw newE(args);
        }
        //noinspection unchecked
        return (T) target;
    }

    @Contract("null,_,_->fail;!null,_,_->!null")
    default <T extends Iterable<?>> T assertNotEmptyWithData(
        final Iterable<?> target,
        final Object data,
        final Object... args
    ) {
        if (CollectionUtil.isEmpty(target)) {
            throw newEWithData(data, args);
        }
        //noinspection unchecked
        return (T) target;
    }

    @Contract("null,_,_->fail;!null,_,_->!null")
    default <T extends Iterable<?>> T assertNotEmptyWithDataSupplier(
        final Iterable<?> target,
        final Supplier<Object> dataSupplier,
        final Object... args
    ) {
        if (CollectionUtil.isEmpty(target)) {
            throw newEWithData(dataSupplier.get(), args);
        }
        //noinspection unchecked
        return (T) target;
    }

    // assert empty

    default void assertEmpty(final Iterable<?> target, final Object... args) {
        if (CollectionUtil.isNotEmpty(target)) {
            throw newE(args);
        }
    }

    default void assertEmptyWithData(
        final Iterable<?> target,
        final Object data,
        final Object... args
    ) {
        if (CollectionUtil.isNotEmpty(target)) {
            throw newEWithData(data, args);
        }
    }

    default void assertEmptyWithDataSupplier(
        final Iterable<?> target,
        final Supplier<Object> dataSupplier,
        final Object... args
    ) {
        if (CollectionUtil.isNotEmpty(target)) {
            throw newEWithData(dataSupplier.get(), args);
        }
    }
}
