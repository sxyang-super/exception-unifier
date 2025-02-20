package io.github.sxyangsuper.exceptionunifier.mavenplugin.handler;

import io.github.sxyangsuper.exceptionunifier.mavenplugin.source.IExceptionCodeSource;
import org.apache.maven.plugin.MojoExecutionException;

public class PushHandler {
    private final IExceptionCodeSource exceptionCodeSource;
    private final SyncResult syncResult;

    public PushHandler(final IExceptionCodeSource exceptionCodeSource, final SyncResult syncResult) {
        this.exceptionCodeSource = exceptionCodeSource;
        this.syncResult = syncResult;
    }

    public void push() throws MojoExecutionException {
        exceptionCodeSource.push(this.syncResult);
    }
}
