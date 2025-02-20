package io.github.sxyangsuper.exceptionunifier.mavenplugin.handler;

import cn.hutool.core.map.MapUtil;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.source.SourceType;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class MojoConfigurationTest {

    @Test
    void should_construct_successfully() {
        MavenProject mavenProject = Mockito.mock(MavenProject.class);
        MojoConfiguration mojoConfiguration = new MojoConfiguration(SourceType.BASE,
                "SAMPLE",
                "http://localhost",
                mavenProject,
                MapUtil.empty()
        );

        assertEquals(SourceType.BASE, mojoConfiguration.getSourceType());
        assertEquals("SAMPLE", mojoConfiguration.getExceptionCodePrefix());
        assertEquals("http://localhost", mojoConfiguration.getRemoteBaseURL());
        assertEquals(mavenProject, mojoConfiguration.getMavenProject());
    }
}
