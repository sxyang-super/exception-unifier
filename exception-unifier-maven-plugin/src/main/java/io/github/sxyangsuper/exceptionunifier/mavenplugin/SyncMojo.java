package io.github.sxyangsuper.exceptionunifier.mavenplugin;

import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.CheckHandler;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.CheckResult;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.MojoConfiguration;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.ResultHandler;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.SyncHandler;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.SyncResult;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.source.IExceptionCodeSource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "sync", defaultPhase = LifecyclePhase.COMPILE)
public class SyncMojo extends AbstractExceptionUnifierMojo {

    @Override
    public void execute() throws MojoExecutionException {
        final MojoConfiguration mojoConfiguration = super.getMojoConfiguration();
        final CheckResult checkResult = this.getCheckResult();
        final IExceptionCodeSource exceptionSource = super.getExceptionCodeSource(mojoConfiguration);

        final SyncHandler syncHandler = new SyncHandler(
                exceptionSource,
                checkResult,
                this.getMojoConfiguration()
        );

        final SyncResult syncResult = syncHandler.syncExceptionEnums();

        new ResultHandler(this.getMojoConfiguration())
                .storeSyncResult(syncResult);
    }

    private CheckResult getCheckResult() throws MojoExecutionException {
        final ResultHandler resultHandler = new ResultHandler(this.getMojoConfiguration());
        final CheckResult checkResult = resultHandler.getCheckResult();

        if (checkResult != null) {
            getLog().info("check result exist");
            return checkResult;
        }

        final CheckHandler checkHandler = new CheckHandler(this.getMojoConfiguration());
        return checkHandler.checkExceptionEnums();
    }
}
