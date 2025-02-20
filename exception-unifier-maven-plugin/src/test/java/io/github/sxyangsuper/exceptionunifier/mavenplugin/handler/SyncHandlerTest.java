package io.github.sxyangsuper.exceptionunifier.mavenplugin.handler;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.fixture.MojoConfigurationFixture;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.fixture.SyncHandlerTest.SyncExceptionEnumsTest.should_modify_successfully.SampleExceptionEnum;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.source.IExceptionCodeSource;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Collections;

import static io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.fixture.MojoConfigurationFixture.getMockMojoConfiguration;

public class SyncHandlerTest {

    String TEST_CASE_CORRESPONDING_CLASS_DIRECTORY = null;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        String testCaseRelativeDirectory = testInfo.getTestMethod().get().getDeclaringClass().getName()
                .replace(this.getClass().getPackage().getName(), "")
                .replace("$", File.separator)
                .replaceFirst(".", "");
        TEST_CASE_CORRESPONDING_CLASS_DIRECTORY = String.join(File.separator,
                System.getProperty("user.dir"),
                "target",
                "test-classes",
                this.getClass().getPackage().getName().replace(".", File.separator),
                "fixture",
                testCaseRelativeDirectory,
                testInfo.getTestMethod().get().getName());
    }

    @Nested
    class SyncExceptionEnumsTest {

        @Test
        void should_do_nothing_given_exception_enums_from_check_result_is_empty() {
            final CheckResult checkResult = Mockito.mock(CheckResult.class);
            Mockito.when(checkResult.getExceptionEnums())
                    .thenReturn(Collections.emptyList());

            final SyncHandler syncHandler = new SyncHandler(null, checkResult, null);

            Assertions.assertDoesNotThrow(syncHandler::syncExceptionEnums);
        }

        @Test
        void should_modify_successfully() throws MojoExecutionException {
            final CheckResult checkResult = Mockito.mock(CheckResult.class);
            final ExceptionEnum exceptionEnum = Mockito.mock(ExceptionEnum.class);
            Mockito.when(exceptionEnum.getClassFilePath())
                    .thenReturn(String.join(File.separator, TEST_CASE_CORRESPONDING_CLASS_DIRECTORY, "SampleExceptionEnum.class"));
            Mockito.when(exceptionEnum.getSource())
                    .thenReturn("SAMPLE-SOURCE");
            Mockito.when(exceptionEnum.getCodeConstructorParametersIndex())
                    .thenReturn(3);
            Mockito.when(exceptionEnum.getMessageConstructorParametersIndex())
                    .thenReturn(4);
            Mockito.when(exceptionEnum.getTotalConstructorParameterCount())
                    .thenReturn(5);

            Mockito.when(checkResult.getExceptionEnums())
                    .thenReturn(ListUtil.of(
                            exceptionEnum
                    ));
            final IExceptionCodeSource exceptionCodeSource = Mockito.mock(IExceptionCodeSource.class);
            Mockito.when(exceptionCodeSource.getExceptionCodePrefix())
                    .thenReturn("SAMPLE");

            final MojoConfiguration mockMojoConfiguration = MojoConfigurationFixture.getMojoConfigurationWithMavenProjectConsumer(mavenProject -> {
                Mockito.when(mavenProject.getGroupId())
                        .thenReturn("com.example");
                Mockito.when(mavenProject.getArtifactId())
                        .thenReturn("test");
            });
            final SyncHandler syncHandler = new SyncHandler(exceptionCodeSource, checkResult, mockMojoConfiguration);

            Assertions.assertDoesNotThrow(syncHandler::syncExceptionEnums);
            Assertions.assertDoesNotThrow(syncHandler::syncExceptionEnums);

            Assertions.assertEquals("SAMPLE:SAMPLE-SOURCE:001", SampleExceptionEnum.TEST.getCode());
        }

        @Test
        void should_throw_exception_given_read_class_file_fail() throws IOException, MojoExecutionException {
            File file = new File(String.join(File.separator, TEST_CASE_CORRESPONDING_CLASS_DIRECTORY, "SampleExceptionEnum.class"));
            try (final MockedStatic<FileUtil> fileUtilMockedStatic = Mockito.mockStatic(FileUtil.class)) {
                fileUtilMockedStatic.when(() -> FileUtil.loopFiles(ArgumentMatchers.anyString()))
                        .then((invocation -> {
                            if (!invocation.getMethod().getName().equals("getInputStream")) {
                                return ListUtil.of(file);
                            }

                            throw new IORuntimeException("mocked exception");
                        }));

                final CheckResult checkResult = Mockito.mock(CheckResult.class);
                final ExceptionEnum exceptionEnum = Mockito.mock(ExceptionEnum.class);
                Mockito.when(exceptionEnum.getClassFilePath())
                        .thenReturn(String.join(File.separator, TEST_CASE_CORRESPONDING_CLASS_DIRECTORY, "SampleExceptionEnum.class"));
                Mockito.when(exceptionEnum.getSource())
                        .thenReturn("SAMPLE-SOURCE");
                Mockito.when(exceptionEnum.getCodeConstructorParametersIndex())
                        .thenReturn(3);
                Mockito.when(exceptionEnum.getMessageConstructorParametersIndex())
                        .thenReturn(4);
                Mockito.when(exceptionEnum.getTotalConstructorParameterCount())
                        .thenReturn(5);

                Mockito.when(checkResult.getExceptionEnums())
                        .thenReturn(ListUtil.of(
                                exceptionEnum
                        ));
                final IExceptionCodeSource exceptionCodeSource = Mockito.mock(IExceptionCodeSource.class);
                Mockito.when(exceptionCodeSource.getExceptionCodePrefix())
                        .thenReturn("SAMPLE");

                final MojoConfiguration mockMojoConfiguration = MojoConfigurationFixture.getMojoConfigurationWithMavenProjectConsumer(mavenProject -> {
                    Mockito.when(mavenProject.getGroupId())
                            .thenReturn("com.example");
                    Mockito.when(mavenProject.getArtifactId())
                            .thenReturn("test");
                });
                final SyncHandler syncHandler = new SyncHandler(exceptionCodeSource, checkResult, mockMojoConfiguration);
                Assertions.assertThrows(MojoExecutionException.class, syncHandler::syncExceptionEnums);
            }
        }
    }
}