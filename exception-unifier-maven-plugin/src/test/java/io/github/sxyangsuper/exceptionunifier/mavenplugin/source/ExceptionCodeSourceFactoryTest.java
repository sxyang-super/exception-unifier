package io.github.sxyangsuper.exceptionunifier.mavenplugin.source;

import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.MojoConfiguration;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.util.concurrent.atomic.AtomicReference;

class ExceptionCodeSourceFactoryTest {

    @Nested
    class CreateExceptionCodeSourceTest {

        @Test
        void should_throw_exception_given_source_type_of_configuration_is_null() {
            MojoConfiguration mojoConfiguration = Mockito.mock(MojoConfiguration.class);
            Mockito.when(mojoConfiguration.getSourceType()).thenReturn(null);
            Assertions.assertThrows(IllegalArgumentException.class, () -> ExceptionCodeSourceFactory.createExceptionCodeSource(mojoConfiguration));
        }

        private <T extends IExceptionCodeSource> void assertCorrectExceptionSourceIsCreatedDueToSourceType(SourceType sourceType, Class<T> exceptionCodeSourceClass) throws MojoExecutionException {
            MojoConfiguration mojoConfiguration = Mockito.mock(MojoConfiguration.class);
            Mockito.when(mojoConfiguration.getSourceType()).thenReturn(sourceType);
            AtomicReference<T> baseExceptionCodeSource = new AtomicReference<>();

            try (MockedConstruction<T> baseExceptionCodeSourceMockedConstruction = Mockito.mockConstruction(exceptionCodeSourceClass, (mock, context) -> baseExceptionCodeSource.set(mock))) {
                IExceptionCodeSource exceptionCodeSource = ExceptionCodeSourceFactory.createExceptionCodeSource(mojoConfiguration);

                Assertions.assertEquals(baseExceptionCodeSource.get(), exceptionCodeSource);
                Assertions.assertEquals(1, baseExceptionCodeSourceMockedConstruction.constructed().size());
            }
        }

        @Test
        void should_get_base_exception_code_source_given_source_type_is_base() throws MojoExecutionException {
            SourceType sourceType = SourceType.BASE;
            Class<BaseExceptionCodeSource> exceptionCodeSourceClass = BaseExceptionCodeSource.class;

            assertCorrectExceptionSourceIsCreatedDueToSourceType(sourceType, exceptionCodeSourceClass);
        }

        @Test
        void should_get_remote_exception_code_source_given_source_type_is_remote() throws MojoExecutionException {
            assertCorrectExceptionSourceIsCreatedDueToSourceType(SourceType.REMOTE, RemoteExceptionCodeSource.class);
        }
    }
}
