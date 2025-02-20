package io.github.sxyangsuper.exceptionunifier.mavenplugin;

import cn.hutool.core.util.ReflectUtil;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.MojoConfiguration;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.PushHandler;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.ResultHandler;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.SyncResult;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.source.ExceptionCodeSourceFactory;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.source.IExceptionCodeSource;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.source.SourceType;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.mockito.Mockito.mockConstruction;

class PushMojoTest {

    public static final SourceType SOURCE_TYPE = SourceType.BASE;
    public static final String EXCEPTION_CODE_PREFIX = "ABC";
    public static final String REMOTE_BASE_URL = "http://localhost";
    private MavenProject mavenProject;
    PushMojo pushMojo = null;

    @BeforeEach
    void setUp() {
        pushMojo = new PushMojo();

        mavenProject = Mockito.mock(MavenProject.class);
        ReflectUtil.setFieldValue(pushMojo, "sourceType", SOURCE_TYPE);
        ReflectUtil.setFieldValue(pushMojo, "exceptionCodePrefix", EXCEPTION_CODE_PREFIX);
        ReflectUtil.setFieldValue(pushMojo, "remoteBaseURL", REMOTE_BASE_URL);
        ReflectUtil.setFieldValue(pushMojo, "mavenProject", mavenProject);
    }

    @Test
    void should_push_successfully() throws MojoExecutionException {
        final SyncResult syncResult = Mockito.mock(SyncResult.class);
        final ArgumentCaptor<MojoConfiguration> mojoConfigurationArgumentCaptor = ArgumentCaptor.forClass(MojoConfiguration.class);
        final IExceptionCodeSource exceptionCodeSource = Mockito.mock(IExceptionCodeSource.class);

        try (MockedConstruction<MojoConfiguration> mojoConfigurationMockedConstruction = mockConstruction(MojoConfiguration.class, (mock, context) -> {
            Assertions.assertEquals(SOURCE_TYPE, context.arguments().get(0));
            Assertions.assertEquals(EXCEPTION_CODE_PREFIX, context.arguments().get(1));
            Assertions.assertEquals(REMOTE_BASE_URL, context.arguments().get(2));
            Assertions.assertEquals(mavenProject, context.arguments().get(3));
        }); final MockedConstruction<ResultHandler> resultHandlerMockedConstruction = Mockito.mockConstruction(ResultHandler.class, (mock, context) -> {
            Assertions.assertEquals(mojoConfigurationMockedConstruction.constructed().get(0), context.arguments().get(0));
            Mockito.when(mock.getSyncResult())
                    .thenReturn(syncResult);
        }); final MockedStatic<ExceptionCodeSourceFactory> exceptionCodeSourceFactoryMockedStatic = Mockito.mockStatic(ExceptionCodeSourceFactory.class);
             final MockedConstruction<PushHandler> pushHandlerMockedConstruction = mockConstruction(PushHandler.class, (mock, context) -> {
                 Assertions.assertEquals(exceptionCodeSource, context.arguments().get(0));
                 Assertions.assertEquals(syncResult, context.arguments().get(1));
             })
        ) {
            exceptionCodeSourceFactoryMockedStatic.when(() -> ExceptionCodeSourceFactory.createExceptionCodeSource(mojoConfigurationArgumentCaptor.capture()))
                    .thenReturn(exceptionCodeSource);

            pushMojo.execute();

            Mockito.verify(resultHandlerMockedConstruction.constructed().get(0), Mockito.times(1))
                    .getSyncResult();
            Assertions.assertEquals(mojoConfigurationMockedConstruction.constructed().get(0), mojoConfigurationArgumentCaptor.getValue());
            Mockito.verify(exceptionCodeSource, Mockito.times(1)).setupAndCheck(mojoConfigurationMockedConstruction.constructed().get(0));
            Mockito.verify(pushHandlerMockedConstruction.constructed().get(0), Mockito.times(1))
                    .push();
        }
    }

    @Test
    void should_skip_push_given_sync_result_does_not_exist() throws MojoExecutionException {
        try (MockedConstruction<MojoConfiguration> mojoConfigurationMockedConstruction = mockConstruction(MojoConfiguration.class, (mock, context) -> {
            Assertions.assertEquals(SOURCE_TYPE, context.arguments().get(0));
            Assertions.assertEquals(EXCEPTION_CODE_PREFIX, context.arguments().get(1));
            Assertions.assertEquals(REMOTE_BASE_URL, context.arguments().get(2));
            Assertions.assertEquals(mavenProject, context.arguments().get(3));
        }); final MockedConstruction<ResultHandler> resultHandlerMockedConstruction = Mockito.mockConstruction(ResultHandler.class, (mock, context) -> {
            Assertions.assertEquals(mojoConfigurationMockedConstruction.constructed().get(0), context.arguments().get(0));
            Mockito.when(mock.getSyncResult())
                    .thenReturn(null);
        })) {
            pushMojo.execute();

            Mockito.verify(resultHandlerMockedConstruction.constructed().get(0), Mockito.times(1))
                    .getSyncResult();
        }
    }
}