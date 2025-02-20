package io.github.sxyangsuper.exceptionunifier.base.asserts;

import cn.hutool.core.util.StrUtil;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class AssertUtil {

    /* default */
    static boolean isNotCharSequenceOrBlank(final Object target) {
        return !(target instanceof CharSequence) || StrUtil.isBlank((CharSequence) target);
    }

    /* default */
    static boolean isNotCharSequenceOrNotBlank(final Object target) {
        return !(target instanceof CharSequence) || !StrUtil.isBlank((CharSequence) target);
    }

    /* default */
    static boolean doesNotMatch(final Pattern pattern, final CharSequence target) {
        return pattern == null || target == null || !pattern.matcher(target).matches();
    }
}
