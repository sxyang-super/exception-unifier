package io.github.sxyangsuper.exceptionunifier.base.asserts;

import io.github.sxyangsuper.exceptionunifier.base.BaseException;
import io.github.sxyangsuper.exceptionunifier.base.IExceptionSupplier;
import org.jetbrains.annotations.Contract;

import java.util.function.Supplier;

import static io.github.sxyangsuper.exceptionunifier.base.asserts.AssertUtil.isNotCharSequenceOrBlank;
import static io.github.sxyangsuper.exceptionunifier.base.asserts.AssertUtil.isNotCharSequenceOrNotBlank;

public interface ICharSequenceAsserts<E extends BaseException> extends IExceptionSupplier<E> {
    // assert not blank

    @Contract("null,_-> fail;!null,_->!null")
    default <T extends CharSequence> T assertNotBlank(final Object target, final Object... args) {
        if (isNotCharSequenceOrBlank(target)) {
            throw newE(args);
        }
        //noinspection unchecked
        return (T) target;
    }

    @Contract("null,_,_-> fail;!null,_,_->!null")
    default <T extends CharSequence> T assertNotBlankWithData(
            final Object target,
            final Object data,
            final Object... args
    ) {
        if (isNotCharSequenceOrBlank(target)) {
            throw newEWithData(data, args);
        }
        //noinspection unchecked
        return (T) target;
    }

    @Contract("null,_,_-> fail;!null,_,_->!null")
    default <T extends CharSequence> T assertNotBlankWithDataSupplier(
            final Object target,
            final Supplier<Object> dataSupplier,
            final Object... args
    ) {
        if (isNotCharSequenceOrBlank(target)) {
            throw newEWithData(dataSupplier.get(), args);
        }
        //noinspection unchecked
        return (T) target;
    }

    // assert blank

    default <T extends CharSequence> T assertBlank(final Object target, final Object... args) {
        if (isNotCharSequenceOrNotBlank(target)) {
            throw newE(args);
        }
        //noinspection unchecked
        return (T) target;
    }

    default <T extends CharSequence> T assertBlankWithData(
            final Object target,
            final Object data,
            final Object... args
    ) {
        if (isNotCharSequenceOrNotBlank(target)) {
            throw newEWithData(data, args);
        }
        //noinspection unchecked
        return (T) target;
    }

    default <T extends CharSequence> T assertBlankWithDataSupplier(
            final Object target,
            final Supplier<Object> dataSupplier,
            final Object... args
    ) {
        if (isNotCharSequenceOrNotBlank(target)) {
            throw newEWithData(dataSupplier.get(), args);
        }
        //noinspection unchecked
        return (T) target;
    }
}
