package io.github.sxyangsuper.exceptionunifier.processor;


import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

import static io.github.sxyangsuper.exceptionunifier.processor.Consts.CLASS_QN_NAME_ORG_JETBRAINS_JPS_JAVAC_API_WRAPPERS;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
class UncheckedUtil {

    /*default*/
    static <T> T jbUnwrapProcessingEnvironment(final T target) {
        T unwrapped = null;
        try {
            final Class<?> apiWrappers = target.getClass().getClassLoader().loadClass(CLASS_QN_NAME_ORG_JETBRAINS_JPS_JAVAC_API_WRAPPERS);
            final Method unwrapMethod = apiWrappers.getDeclaredMethod("unwrap", Class.class, Object.class);
            //noinspection unchecked
            unwrapped = ((Class<? extends T>) javax.annotation.processing.ProcessingEnvironment.class).cast(unwrapMethod.invoke(null, javax.annotation.processing.ProcessingEnvironment.class, target));
        } catch (Exception ignored) {
        }
        return unwrapped != null ? unwrapped : target;
    }
}
