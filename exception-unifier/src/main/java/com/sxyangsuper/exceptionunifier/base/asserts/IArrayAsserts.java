package com.sxyangsuper.exceptionunifier.base.asserts;

import cn.hutool.core.util.ArrayUtil;
import com.sxyangsuper.exceptionunifier.base.BaseException;
import com.sxyangsuper.exceptionunifier.base.IExceptionSupplier;
import org.jetbrains.annotations.Contract;

import java.util.function.Supplier;

public interface IArrayAsserts<E extends BaseException> extends IExceptionSupplier<E> {
    // solve duplication
    String NULL_FAIL_NULL_NULL = "null,_,_->fail;!null,_,_->!null";

    // assert not empty

    @Contract("null,_->fail;!null,_->!null")
    default <T> T[] assertNotEmpty(final Object[] target, final Object... args) {
        if (ArrayUtil.isEmpty(target)) {
            throw newE(args);
        }
        //noinspection unchecked
        return (T[]) target;
    }

    @Contract(NULL_FAIL_NULL_NULL)
    default <T> T[] assertNotEmptyWithData(
        final Object[] target,
        final Object data,
        final Object... args
    ) {
        if (ArrayUtil.isEmpty(target)) {
            throw newEWithData(data, args);
        }
        //noinspection unchecked
        return (T[]) target;
    }

    @Contract(NULL_FAIL_NULL_NULL)
    default <T> T[] assertNotEmptyWithDataSupplier(
        final Object[] target,
        final Supplier<Object> dataSupplier,
        final Object... args
    ) {
        if (ArrayUtil.isEmpty(target)) {
            throw newEWithData(dataSupplier.get(), args);
        }
        //noinspection unchecked
        return (T[]) target;
    }

    // assert empty

    @Contract("null,_->fail;!null,_->!null")
    default <T> T[] assertEmpty(final Object[] target, final Object... args) {
        if (ArrayUtil.isNotEmpty(target)) {
            throw newE(args);
        }
        //noinspection unchecked
        return (T[]) target;
    }

    @Contract(NULL_FAIL_NULL_NULL)
    default <T> T[] assertEmptyWithData(
        final Object[] target,
        final Object data,
        final Object... args
    ) {
        if (ArrayUtil.isNotEmpty(target)) {
            throw newEWithData(data, args);
        }
        //noinspection unchecked
        return (T[]) target;
    }

    @Contract(NULL_FAIL_NULL_NULL)
    default <T> T[] assertEmptyWithDataSupplier(
        final Object[] target,
        final Supplier<Object> dataSupplier,
        final Object... args
    ) {
        if (ArrayUtil.isNotEmpty(target)) {
            throw newEWithData(dataSupplier.get(), args);
        }
        //noinspection unchecked
        return (T[]) target;
    }
}
