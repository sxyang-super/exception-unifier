package com.sxyangsuper.exceptionunifier.base.asserts;

import cn.hutool.core.util.ObjectUtil;
import com.sxyangsuper.exceptionunifier.base.BaseException;
import com.sxyangsuper.exceptionunifier.base.IExceptionSupplier;

public interface IComparisonAsserts<E extends BaseException> extends IExceptionSupplier<E> {
    // assert equal

    default void assertEqual(
        final Object source,
        final Object target,
        final Object... args
    ) {
        if (ObjectUtil.notEqual(source, target)) {
            throw newE(args);
        }
    }

    default void assertEqualWithData(
        final Object source,
        final Object target,
        final Object data,
        final Object... args
    ) {
        if (ObjectUtil.notEqual(source, target)) {
            throw newEWithData(data, args);
        }
    }

    // assert not equal

    default void assertNotEqual(
        final Object source,
        final Object target,
        final Object... args
    ) {
        if (ObjectUtil.equal(source, target)) {
            throw newE(args);
        }
    }

    default void assertNotEqualWithData(
        final Object source,
        final Object target,
        final Object data,
        final Object... args
    ) {
        if (ObjectUtil.equal(source, target)) {
            throw newEWithData(data, args);
        }
    }
}
