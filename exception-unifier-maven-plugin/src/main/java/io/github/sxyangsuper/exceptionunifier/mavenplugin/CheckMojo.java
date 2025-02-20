package io.github.sxyangsuper.exceptionunifier.mavenplugin;

import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.CheckHandler;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.CheckResult;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.MojoConfiguration;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.ResultHandler;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "check", defaultPhase = LifecyclePhase.COMPILE)
public class CheckMojo extends AbstractExceptionUnifierMojo {

    @Override
    public void execute() throws MojoExecutionException {
        final MojoConfiguration mojoConfiguration = super.getMojoConfiguration();

        super.getExceptionCodeSource(mojoConfiguration);

        final ResultHandler resultHandler = new ResultHandler(mojoConfiguration);
        resultHandler.clearCheckResult();

        final CheckHandler checkHandler = new CheckHandler(mojoConfiguration);
        final CheckResult checkResult = checkHandler.checkExceptionEnums();

        resultHandler.storeCheckResult(checkResult);
    }
}
