package io.github.sxyangsuper.exceptionunifier.mavenplugin.source;

import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.MojoConfiguration;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.SyncResult;
import org.apache.maven.plugin.MojoExecutionException;

public interface IExceptionCodeSource {
    void setupAndCheck(MojoConfiguration mojoConfiguration) throws MojoExecutionException;

    String getExceptionCodePrefix() throws MojoExecutionException;

    void push(SyncResult syncResult) throws MojoExecutionException;
}
