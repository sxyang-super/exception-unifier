package io.github.sxyangsuper.exceptionunifier.mavenplugin;

import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.MojoConfiguration;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.source.ExceptionCodeSourceFactory;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.source.IExceptionCodeSource;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.source.SourceType;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public abstract class AbstractExceptionUnifierMojo extends AbstractMojo {
    @Parameter(property = "exceptionunifier.sourceType", required = true)
    protected SourceType sourceType;
    @Parameter(property = "exceptionunifier.exceptionCodePrefix")
    protected String exceptionCodePrefix;
    @Parameter(property = "exceptionunifier.remoteBaseURL")
    protected String remoteBaseURL;
    @Parameter(property = "exceptionunifier.remoteQuery")
    protected Map<String, String> remoteQuery;
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject mavenProject;

    private MojoConfiguration mojoConfiguration;

    protected IExceptionCodeSource getExceptionCodeSource(final MojoConfiguration mojoConfiguration) throws MojoExecutionException {
        final IExceptionCodeSource exceptionCodeSource = ExceptionCodeSourceFactory.createExceptionCodeSource(
                mojoConfiguration
        );
        exceptionCodeSource.setupAndCheck(mojoConfiguration);

        return exceptionCodeSource;
    }

    @NotNull
    protected MojoConfiguration getMojoConfiguration() {
        if (this.mojoConfiguration != null) {
            return this.mojoConfiguration;
        }
        return this.mojoConfiguration = new MojoConfiguration(sourceType, exceptionCodePrefix, remoteBaseURL, mavenProject, remoteQuery);
    }
}
