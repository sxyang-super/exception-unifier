package io.github.sxyangsuper.exceptionunifier.mavenplugin.source;

import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.MojoConfiguration;
import org.jetbrains.annotations.NotNull;

final public class ExceptionCodeSourceFactory {

    private ExceptionCodeSourceFactory() {
    }

    @NotNull
    public static IExceptionCodeSource createExceptionCodeSource(final MojoConfiguration mojoConfiguration) {
        final SourceType sourceType = mojoConfiguration.getSourceType();

        if (sourceType == null) {
            throw new IllegalArgumentException("Source type must be specified");
        }

        if (sourceType == SourceType.BASE) {
            return new BaseExceptionCodeSource();
        }

        return new RemoteExceptionCodeSource();
    }
}
