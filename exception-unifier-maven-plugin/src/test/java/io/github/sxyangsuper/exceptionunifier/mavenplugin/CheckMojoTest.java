package io.github.sxyangsuper.exceptionunifier.mavenplugin;

import cn.hutool.core.util.ReflectUtil;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.CheckHandler;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.CheckResult;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.MojoConfiguration;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.ResultHandler;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.times;

class CheckMojoTest {

    public static final SourceType SOURCE_TYPE = SourceType.BASE;
    public static final String EXCEPTION_CODE_PREFIX = "ABC";
    public static final String REMOTE_BASE_URL = "http://localhost";
    private MavenProject mavenProject;
    CheckMojo checkMojo = null;

    @BeforeEach
    void setUp() {
        checkMojo = new CheckMojo();

        mavenProject = Mockito.mock(MavenProject.class);
        ReflectUtil.setFieldValue(checkMojo, "sourceType", SOURCE_TYPE);
        ReflectUtil.setFieldValue(checkMojo, "exceptionCodePrefix", EXCEPTION_CODE_PREFIX);
        ReflectUtil.setFieldValue(checkMojo, "remoteBaseURL", REMOTE_BASE_URL);
        ReflectUtil.setFieldValue(checkMojo, "mavenProject", mavenProject);
    }

    @Test
    void should_execute_successfully() throws MojoExecutionException {
        final ArgumentCaptor<MojoConfiguration> mojoConfigurationArgumentCaptor = ArgumentCaptor.forClass(MojoConfiguration.class);
        final IExceptionCodeSource exceptionCodeSource = mock(IExceptionCodeSource.class);
        final AtomicReference<MojoConfiguration> checkHandlerConstructorArgumentReference = new AtomicReference<>();
        final AtomicReference<MojoConfiguration> resultHandlerConstructorArgumentReference = new AtomicReference<>();
        final ArgumentCaptor<CheckResult> checkResultArgumentCapture = ArgumentCaptor.forClass(CheckResult.class);
        final CheckResult checkResult = Mockito.mock(CheckResult.class);

        try (
                MockedConstruction<MojoConfiguration> mojoConfigurationMockedConstruction = mockConstruction(MojoConfiguration.class, (mock, context) -> {
                    Assertions.assertEquals(SOURCE_TYPE, context.arguments().get(0));
                    Assertions.assertEquals(EXCEPTION_CODE_PREFIX, context.arguments().get(1));
                    Assertions.assertEquals(REMOTE_BASE_URL, context.arguments().get(2));
                    Assertions.assertEquals(mavenProject, context.arguments().get(3));
                }); MockedStatic<ExceptionCodeSourceFactory> exceptionCodeSourceFactoryMockedStatic = Mockito.mockStatic(ExceptionCodeSourceFactory.class);
                MockedConstruction<CheckHandler> checkHandlerMockedConstruction = mockConstruction(CheckHandler.class,
                        (mock, context) -> {
                            checkHandlerConstructorArgumentReference.set((MojoConfiguration) context.arguments().get(0));

                            Mockito.when(mock.checkExceptionEnums())
                                    .thenReturn(checkResult);
                        });
                MockedConstruction<ResultHandler> mojoResultHandlerMockedConstruction = mockConstruction(ResultHandler.class, (mock, context) -> {
                    Mockito.doNothing().when(mock).storeCheckResult(checkResultArgumentCapture.capture());
                    Mockito.doNothing().when(mock).clearCheckResult();
                    resultHandlerConstructorArgumentReference.set((MojoConfiguration) context.arguments().get(0));
                })
        ) {
            exceptionCodeSourceFactoryMockedStatic.when(() -> ExceptionCodeSourceFactory.createExceptionCodeSource(mojoConfigurationArgumentCaptor.capture()))
                    .thenReturn(exceptionCodeSource);
            Mockito.doNothing().when(exceptionCodeSource).setupAndCheck(mojoConfigurationArgumentCaptor.capture());


            checkMojo.execute();

            Assertions.assertEquals(1, mojoConfigurationMockedConstruction.constructed().size());
            Assertions.assertEquals(1, checkHandlerMockedConstruction.constructed().size());
            Assertions.assertEquals(1, mojoResultHandlerMockedConstruction.constructed().size());

            List<MojoConfiguration> mojoConfigurations = mojoConfigurationArgumentCaptor.getAllValues();
            Assertions.assertEquals(2, mojoConfigurations.size());

            Set<MojoConfiguration> deduplicatedMojoConfigurations = new HashSet<>(mojoConfigurations);
            Assertions.assertEquals(1, deduplicatedMojoConfigurations.size());

            MojoConfiguration localHandlerConstructorArgument = checkHandlerConstructorArgumentReference.get();
            Assertions.assertNotNull(localHandlerConstructorArgument);
            Assertions.assertEquals(localHandlerConstructorArgument, mojoConfigurations.get(0));

            MojoConfiguration resultHandlerConstructorArgument = resultHandlerConstructorArgumentReference.get();
            Assertions.assertNotNull(resultHandlerConstructorArgument);
            Assertions.assertEquals(resultHandlerConstructorArgument, mojoConfigurations.get(0));

            Mockito.verify(checkHandlerMockedConstruction.constructed().get(0), times(1)).checkExceptionEnums();
            Assertions.assertEquals(checkResult, checkResultArgumentCapture.getValue());
            Mockito.verify(mojoResultHandlerMockedConstruction.constructed().get(0), times(1)).clearCheckResult();
        }
    }
}
