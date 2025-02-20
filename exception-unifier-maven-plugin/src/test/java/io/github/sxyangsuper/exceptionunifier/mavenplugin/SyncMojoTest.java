package io.github.sxyangsuper.exceptionunifier.mavenplugin;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.CheckHandler;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.CheckResult;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.MojoConfiguration;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.ResultHandler;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.SyncHandler;
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.times;

public class SyncMojoTest {

    public static final SourceType SOURCE_TYPE = SourceType.BASE;
    public static final String EXCEPTION_CODE_PREFIX = "ABC";
    public static final String REMOTE_BASE_URL = "http://localhost";
    private MavenProject mavenProject;
    SyncMojo syncMojo = null;

    @BeforeEach
    void setUp() {
        syncMojo = new SyncMojo();

        mavenProject = Mockito.mock(MavenProject.class);
        ReflectUtil.setFieldValue(syncMojo, "sourceType", SOURCE_TYPE);
        ReflectUtil.setFieldValue(syncMojo, "exceptionCodePrefix", EXCEPTION_CODE_PREFIX);
        ReflectUtil.setFieldValue(syncMojo, "remoteBaseURL", REMOTE_BASE_URL);
        ReflectUtil.setFieldValue(syncMojo, "mavenProject", mavenProject);
    }

    @Test
    void should_check_before_sync_given_no_existing_check_result() throws MojoExecutionException {
        final IExceptionCodeSource exceptionCodeSource = mock(IExceptionCodeSource.class);
        final CheckResult checkResult = Mockito.mock(CheckResult.class);
        final ArgumentCaptor<MojoConfiguration> mojoConfigurationArgumentCaptor = ArgumentCaptor.forClass(MojoConfiguration.class);

        try (final MockedConstruction<MojoConfiguration> mojoConfigurationMockedConstruction = Mockito.mockConstruction(MojoConfiguration.class, (mock, context) -> {
            Assertions.assertEquals(SOURCE_TYPE, context.arguments().get(0));
            Assertions.assertEquals(EXCEPTION_CODE_PREFIX, context.arguments().get(1));
            Assertions.assertEquals(REMOTE_BASE_URL, context.arguments().get(2));
            Assertions.assertEquals(mavenProject, context.arguments().get(3));
        });
             MockedConstruction<ResultHandler> resultHandlerMockedConstruction = mockConstruction(ResultHandler.class, (mock, context) -> {
                 Mockito.when(mock.getCheckResult()).thenReturn(null);
                 Assertions.assertEquals(mojoConfigurationMockedConstruction.constructed().get(0), context.arguments().get(0));
             });
             MockedConstruction<CheckHandler> checkHandlerMockedConstruction = mockConstruction(CheckHandler.class,
                     (mock, context) -> {
                         Assertions.assertEquals(mojoConfigurationMockedConstruction.constructed().get(0), context.arguments().get(0));
                         Mockito.when(mock.checkExceptionEnums())
                                 .thenReturn(checkResult);
                     });
             MockedStatic<ExceptionCodeSourceFactory> exceptionCodeSourceFactoryMockedStatic = Mockito.mockStatic(ExceptionCodeSourceFactory.class);
             final MockedConstruction<SyncHandler> syncHandlerMockedConstruction = mockConstruction(SyncHandler.class, (mock, context) -> {
                 Assertions.assertEquals(exceptionCodeSource, context.arguments().get(0));
                 Assertions.assertEquals(checkResult, context.arguments().get(1));
             })
        ) {
            exceptionCodeSourceFactoryMockedStatic.when(() -> ExceptionCodeSourceFactory.createExceptionCodeSource(mojoConfigurationArgumentCaptor.capture()))
                    .thenReturn(exceptionCodeSource);

            syncMojo.execute();

            Mockito.verify(resultHandlerMockedConstruction.constructed().get(0), Mockito.times(1)).getCheckResult();
            Mockito.verify(checkHandlerMockedConstruction.constructed().get(0), Mockito.times(1)).checkExceptionEnums();

            Mockito.verify(exceptionCodeSource, times(1)).setupAndCheck(mojoConfigurationMockedConstruction.constructed().get(0));
            Mockito.verify(syncHandlerMockedConstruction.constructed().get(0), Mockito.times(1)).syncExceptionEnums();

            Assertions.assertEquals(mojoConfigurationMockedConstruction.constructed().get(0), mojoConfigurationArgumentCaptor.getValue());
        }
    }

    @Test
    void should_not_check_again_given_there_is_check_result_already() throws MojoExecutionException {
        final IExceptionCodeSource exceptionCodeSource = mock(IExceptionCodeSource.class);
        final CheckResult checkResult = Mockito.mock(CheckResult.class);
        final ArgumentCaptor<MojoConfiguration> mojoConfigurationArgumentCaptor = ArgumentCaptor.forClass(MojoConfiguration.class);

        try (final MockedConstruction<MojoConfiguration> mojoConfigurationMockedConstruction = Mockito.mockConstruction(MojoConfiguration.class, (mock, context) -> {
            Assertions.assertEquals(SOURCE_TYPE, context.arguments().get(0));
            Assertions.assertEquals(EXCEPTION_CODE_PREFIX, context.arguments().get(1));
            Assertions.assertEquals(REMOTE_BASE_URL, context.arguments().get(2));
            Assertions.assertEquals(mavenProject, context.arguments().get(3));
        });
             MockedConstruction<ResultHandler> resultHandlerMockedConstruction = mockConstruction(ResultHandler.class, (mock, context) -> {
                 Mockito.when(mock.getCheckResult()).thenReturn(checkResult);
                 Assertions.assertEquals(mojoConfigurationMockedConstruction.constructed().get(0), context.arguments().get(0));
             });
             MockedConstruction<CheckHandler> checkHandlerMockedConstruction = mockConstruction(CheckHandler.class);
             MockedStatic<ExceptionCodeSourceFactory> exceptionCodeSourceFactoryMockedStatic = Mockito.mockStatic(ExceptionCodeSourceFactory.class);
             final MockedConstruction<SyncHandler> syncHandlerMockedConstruction = mockConstruction(SyncHandler.class, (mock, context) -> {
                 Assertions.assertEquals(exceptionCodeSource, context.arguments().get(0));
                 Assertions.assertEquals(checkResult, context.arguments().get(1));
             })
        ) {
            exceptionCodeSourceFactoryMockedStatic.when(() -> ExceptionCodeSourceFactory.createExceptionCodeSource(mojoConfigurationArgumentCaptor.capture()))
                    .thenReturn(exceptionCodeSource);

            syncMojo.execute();

            Mockito.verify(resultHandlerMockedConstruction.constructed().get(0), Mockito.times(1)).getCheckResult();
            Assertions.assertTrue(CollUtil.isEmpty(checkHandlerMockedConstruction.constructed()));

            Mockito.verify(exceptionCodeSource, times(1)).setupAndCheck(mojoConfigurationMockedConstruction.constructed().get(0));
            Mockito.verify(syncHandlerMockedConstruction.constructed().get(0), Mockito.times(1)).syncExceptionEnums();

            Assertions.assertEquals(mojoConfigurationMockedConstruction.constructed().get(0), mojoConfigurationArgumentCaptor.getValue());
        }
    }
}