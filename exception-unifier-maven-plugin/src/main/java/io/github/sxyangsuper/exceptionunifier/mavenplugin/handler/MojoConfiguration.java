package io.github.sxyangsuper.exceptionunifier.mavenplugin.handler;

import io.github.sxyangsuper.exceptionunifier.mavenplugin.source.SourceType;
import lombok.Getter;
import org.apache.maven.project.MavenProject;

import java.util.Map;

@Getter
public class MojoConfiguration {
    private final SourceType sourceType;
    private final String exceptionCodePrefix;
    private final String remoteBaseURL;
    private final MavenProject mavenProject;
    private final Map<String, String> remoteQuery;

    public MojoConfiguration(final SourceType sourceType,
                             final String exceptionCodePrefix,
                             final String remoteBaseURL,
                             final MavenProject mavenProject, final Map<String, String> remoteQuery) {
        this.sourceType = sourceType;
        this.exceptionCodePrefix = exceptionCodePrefix;
        this.remoteBaseURL = remoteBaseURL;
        this.mavenProject = mavenProject;
        this.remoteQuery = remoteQuery;
    }
}
