package io.github.sxyangsuper.exceptionunifier.base.asserts;

import io.github.sxyangsuper.exceptionunifier.base.BaseException;
import io.github.sxyangsuper.exceptionunifier.base.IExceptionSupplier;
import org.jetbrains.annotations.Contract;

import java.util.function.Supplier;
import java.util.regex.Pattern;

import static io.github.sxyangsuper.exceptionunifier.base.asserts.AssertUtil.doesNotMatch;

public interface IPatternAsserts<E extends BaseException> extends IExceptionSupplier<E> {
    // assert matches

    @Contract("null,_,_->fail;_,null,_->fail;!null,_,_->!null")
    default <T extends CharSequence> T assertMatches(
        final CharSequence target,
        final Pattern pattern,
        final Object... args
    ) {
        if (doesNotMatch(pattern, target)) {
            throw newE(args);
        }
        //noinspection unchecked
        return (T) target;
    }

    @Contract("null,_,_,_->fail;_,null,_,_->fail;!null,_,_,_->!null")
    default <T extends CharSequence> T assertMatchesWithData(
        final CharSequence target,
        final Pattern pattern,
        final Object data,
        final Object... args
    ) {
        if (doesNotMatch(pattern, target)) {
            throw newEWithData(data, args);
        }
        //noinspection unchecked
        return (T) target;
    }

    @Contract("null,_,_,_->fail;_,null,_,_->fail;!null,_,_,_->!null")
    default <T extends CharSequence> T assertMatchesWithDataSupplier(
        final CharSequence target,
        final Pattern pattern,
        final Supplier<Object> dataSupplier,
        final Object... args
    ) {
        if (doesNotMatch(pattern, target)) {
            throw newEWithData(dataSupplier.get(), args);
        }
        //noinspection unchecked
        return (T) target;
    }

    // assert not matches

    @Contract("!null,_,_->!null")
    default <T extends CharSequence> T assertNotMatches(
        final CharSequence target,
        final Pattern pattern,
        final Object... args
    ) {
        if (!doesNotMatch(pattern, target)) {
            throw newE(args);
        }
        //noinspection unchecked
        return (T) target;
    }

    @Contract("!null,_,_,_->!null")
    default <T extends CharSequence> T assertNotMatchesWithData(
        final CharSequence target,
        final Pattern pattern,
        final Object data,
        final Object... args
    ) {
        if (!doesNotMatch(pattern, target)) {
            throw newEWithData(data, args);
        }
        //noinspection unchecked
        return (T) target;
    }

    @Contract("!null,_,_,_->!null")
    default <T extends CharSequence> T assertNotMatchesWithDataSupplier(
        final CharSequence target,
        final Pattern pattern,
        final Supplier<Object> dataSupplier,
        final Object... args
    ) {
        if (!doesNotMatch(pattern, target)) {
            throw newEWithData(dataSupplier.get(), args);
        }
        //noinspection unchecked
        return (T) target;
    }
}
