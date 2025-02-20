package io.github.sxyangsuper.exceptionunifier.mavenplugin.source;

import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.MojoConfiguration;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BaseExceptionCodeSourceTest {

    @Test
    void should_setup_and_check_successfully() {
        MojoConfiguration mojoConfiguration = Mockito.mock(MojoConfiguration.class);
        Mockito.when(mojoConfiguration.getExceptionCodePrefix())
                .thenReturn("SAMPLE");
        BaseExceptionCodeSource baseExceptionCodeSource = new BaseExceptionCodeSource();

        assertDoesNotThrow(() -> baseExceptionCodeSource
                .setupAndCheck(mojoConfiguration));
    }

    @Test
    void should_throw_exception_given_exception_code_prefix_is_blank() {
        MojoConfiguration mojoConfiguration = Mockito.mock(MojoConfiguration.class);
        Mockito.when(mojoConfiguration.getExceptionCodePrefix())
                .thenReturn("  ");
        BaseExceptionCodeSource baseExceptionCodeSource = new BaseExceptionCodeSource();
        MojoExecutionException mojoExecutionException = assertThrows(MojoExecutionException.class, () -> baseExceptionCodeSource
                .setupAndCheck(mojoConfiguration));

        assertEquals("exception code prefix can not be blank.", mojoExecutionException.getMessage());
    }

    @Test
    void should_get_exception_code_prefix_from_configuration_successfully() throws MojoExecutionException {
        final MojoConfiguration mojoConfiguration = Mockito.mock(MojoConfiguration.class);
        Mockito.when(mojoConfiguration.getExceptionCodePrefix())
                .thenReturn("SAMPLE");

        final BaseExceptionCodeSource baseExceptionCodeSource = new BaseExceptionCodeSource();
        baseExceptionCodeSource.setupAndCheck(mojoConfiguration);

        Assertions.assertEquals("SAMPLE", baseExceptionCodeSource.getExceptionCodePrefix());
    }

    @Test
    void should_do_nothing_when_invoke_push() {
        Assertions.assertDoesNotThrow(() -> new BaseExceptionCodeSource().push(null));
    }
}
