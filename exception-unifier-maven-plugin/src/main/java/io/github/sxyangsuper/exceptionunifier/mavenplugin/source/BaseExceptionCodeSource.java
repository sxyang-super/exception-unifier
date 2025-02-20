package io.github.sxyangsuper.exceptionunifier.mavenplugin.source;

import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.MojoConfiguration;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.SyncResult;
import org.apache.maven.plugin.MojoExecutionException;

public class BaseExceptionCodeSource extends AbstractExceptionCodeSource {
    private MojoConfiguration mojoConfiguration;

    @Override
    public void setupAndCheck(final MojoConfiguration mojoConfiguration) throws MojoExecutionException {
        this.mojoConfiguration = mojoConfiguration;
        this.check();
    }

    @Override
    public String getExceptionCodePrefix() {
        return this.mojoConfiguration.getExceptionCodePrefix();
    }

    @Override
    public void push(final SyncResult syncResult) {
        // do nothing
    }

    private void check() throws MojoExecutionException {
        super.validateExceptionCodePrefix(this.mojoConfiguration.getExceptionCodePrefix());
    }
}
