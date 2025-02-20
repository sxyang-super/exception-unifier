package io.github.sxyangsuper.exceptionunifier.mavenplugin;

import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.PushHandler;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.ResultHandler;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.SyncResult;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.source.ExceptionCodeSourceFactory;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.source.IExceptionCodeSource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "push", defaultPhase = LifecyclePhase.INSTALL)
public class PushMojo extends AbstractExceptionUnifierMojo {

    @Override
    public void execute() throws MojoExecutionException {
        final ResultHandler resultHandler = new ResultHandler(this.getMojoConfiguration());
        final SyncResult syncResult = resultHandler.getSyncResult();
        if (syncResult == null) {
            return;
        }

        final IExceptionCodeSource exceptionCodeSource = ExceptionCodeSourceFactory.createExceptionCodeSource(this.getMojoConfiguration());
        exceptionCodeSource.setupAndCheck(this.getMojoConfiguration());

        new PushHandler(exceptionCodeSource, syncResult).push();
    }
}
