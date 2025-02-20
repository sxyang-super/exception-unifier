package io.github.sxyangsuper.exceptionunifier.mavenplugin.util;

import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.MojoConfiguration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MojoConfigurationUtil {

    public static String getModuleId(final MojoConfiguration mojoConfiguration) {
        return String.join(".", mojoConfiguration.getMavenProject().getGroupId(), mojoConfiguration.getMavenProject().getArtifactId());
    }
}
