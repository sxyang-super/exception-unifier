package io.github.sxyangsuper.exceptionunifier.mavenplugin.handler;

import io.github.sxyangsuper.exceptionunifier.mavenplugin.source.IExceptionCodeSource;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PushHandlerTest {

    @Nested
    class PushTest {

        @Test
        void should_push_successfully() throws MojoExecutionException {
            final IExceptionCodeSource exceptionCodeSource = Mockito.mock(IExceptionCodeSource.class);
            final SyncResult syncResult = Mockito.mock(SyncResult.class);

            final PushHandler pushHandler = new PushHandler(exceptionCodeSource, syncResult);

            pushHandler.push();

            Mockito.verify(exceptionCodeSource, Mockito.times(1)).push(syncResult);
        }
    }

}