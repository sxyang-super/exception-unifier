package io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.fixture;

import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.MojoConfiguration;
import org.apache.maven.model.Build;
import org.apache.maven.project.MavenProject;
import org.mockito.Mockito;

import java.util.function.Consumer;

public class MojoConfigurationFixture {

    public static MojoConfiguration getMockMojoConfiguration(final String mavenProjectBuildOutputDirectory) {
        return getMockMojoConfigurationWithBuildConsumer(
                (build -> Mockito.when(build.getOutputDirectory())
                        .thenReturn(mavenProjectBuildOutputDirectory)
                ));
    }

    public static MojoConfiguration getMockMojoConfigurationWithBuildConsumer(final Consumer<Build> mavenProjectBuildMocker) {
        return getMojoConfigurationWithMavenProjectConsumer(mavenProject -> {
            Build build = Mockito.mock(Build.class);

            Mockito.when(mavenProject.getBuild())
                    .thenReturn(build);
            mavenProject.setBuild(build);
            mavenProjectBuildMocker.accept(build);
        });
    }

    public static MojoConfiguration getMojoConfigurationWithMavenProjectConsumer(final Consumer<MavenProject> mavenProjectConsumer) {
        MojoConfiguration mojoConfiguration = Mockito.mock(MojoConfiguration.class);
        MavenProject mavenProject = Mockito.mock(MavenProject.class);
        Mockito.when(mojoConfiguration.getMavenProject())
                .thenReturn(mavenProject);
        mavenProjectConsumer.accept(mavenProject);
        return mojoConfiguration;
    }
}
