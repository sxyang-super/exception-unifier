package io.github.sxyangsuper.exceptionunifier.mavenplugin.handler;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.fixture.MojoConfigurationFixture;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.fixture.SyncResultFixture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;

class ResultHandlerTest {

    @Nested
    class ClearCheckResultTest {

        @Test
        void should_delete_check_result_successfully() {
            final MojoConfiguration mojoConfiguration = MojoConfigurationFixture.getMockMojoConfigurationWithBuildConsumer(
                    (build -> Mockito.when(build.getDirectory())
                            .thenReturn("target"))
            );
            final ResultHandler resultHandler = new ResultHandler(mojoConfiguration);
            ArgumentCaptor<String> delFilePathArgumentCaptor = ArgumentCaptor.forClass(String.class);

            try (MockedStatic<FileUtil> fileUtilMockedStatic = Mockito.mockStatic(FileUtil.class)) {
                fileUtilMockedStatic.when(() -> FileUtil.del(delFilePathArgumentCaptor.capture()))
                        .thenReturn(true);
                resultHandler.clearCheckResult();
                Assertions.assertEquals(String.join(File.separator, "target", "exception-unifier", "check-result.json"), delFilePathArgumentCaptor.getValue());
            }
        }
    }

    @Nested
    class StoreCheckResultTest {

        @Test
        void should_create_check_result_successfully() {
            final MojoConfiguration mojoConfiguration = MojoConfigurationFixture.getMockMojoConfigurationWithBuildConsumer(
                    (build -> Mockito.when(build.getDirectory())
                            .thenReturn("target"))
            );
            final ResultHandler resultHandler = new ResultHandler(mojoConfiguration);
            final CheckResult checkResult = Mockito.mock(CheckResult.class);
            Mockito.when(checkResult.getExceptionEnums())
                    .thenReturn(ListUtil.of(new ExceptionEnum()
                            .setClassFilePath("mocked class file path")));

            final ArgumentCaptor<String> writeFileContentArgumentCaptor = ArgumentCaptor.forClass(String.class);
            final ArgumentCaptor<String> writeFilePathArgumentCaptor = ArgumentCaptor.forClass(String.class);

            try (MockedStatic<FileUtil> fileUtilMockedStatic = Mockito.mockStatic(FileUtil.class)) {
                fileUtilMockedStatic
                        .when(() -> FileUtil.writeUtf8String(writeFileContentArgumentCaptor.capture(), writeFilePathArgumentCaptor.capture()))
                        .thenReturn(Mockito.mock(File.class));

                resultHandler.storeCheckResult(checkResult);

                Assertions.assertEquals("{\"exceptionEnums\":[{\"exceptionEnumVariables\":[],\"classFilePath\":\"mocked class file path\"}]}", writeFileContentArgumentCaptor.getValue());
                Assertions.assertEquals(
                        String.join(File.separator, "target", "exception-unifier", "check-result.json"),
                        writeFilePathArgumentCaptor.getValue()
                );
            }
        }
    }

    @Nested
    class GetCheckResultTest {

        @Test
        void should_get_check_result_successfully_given_it_exists() {
            final ArgumentCaptor<File> fileArgumentCaptor = ArgumentCaptor.forClass(File.class);
            final CheckResult checkResult = Mockito.mock(CheckResult.class);

            try (final MockedStatic<FileUtil> fileUtilMockedStatic = Mockito.mockStatic(FileUtil.class);
                 final MockedStatic<JSONUtil> jsonUtilMockedStatic = Mockito.mockStatic(JSONUtil.class)) {

                jsonUtilMockedStatic.when(() -> JSONUtil.toBean(ArgumentMatchers.eq("mocked json"), ArgumentMatchers.eq(CheckResult.class)))
                        .thenReturn(checkResult);

                final ResultHandler resultHandler = new ResultHandler(MojoConfigurationFixture.getMockMojoConfigurationWithBuildConsumer(
                        (build) -> Mockito.when(build.getDirectory())
                                .thenReturn("target")
                ));

                try (final MockedConstruction<File> fileMockedConstruction = Mockito.mockConstruction(File.class, (mock, context) -> {
                    Assertions.assertEquals(String.join(File.separator, "target", "exception-unifier", "check-result.json"), context.arguments().get(0));
                    Mockito.when(mock.exists())
                            .thenReturn(true);
                })) {
                    fileUtilMockedStatic.when(() -> FileUtil.readUtf8String(fileArgumentCaptor.capture()))
                            .thenReturn("mocked json");

                    Assertions.assertEquals(checkResult, resultHandler.getCheckResult());
                    Assertions.assertEquals(fileMockedConstruction.constructed().get(0), fileArgumentCaptor.getValue());
                }
            }
        }

        @Test
        void should_return_null_given_check_result_file_does_not_exist() {
            final ResultHandler resultHandler = new ResultHandler(MojoConfigurationFixture.getMockMojoConfigurationWithBuildConsumer(
                    (build) -> Mockito.when(build.getDirectory())
                            .thenReturn("target")
            ));

            try (final MockedConstruction<File> fileMockedConstruction = Mockito.mockConstruction(File.class, (mock, context) -> {
                Assertions.assertEquals(String.join(File.separator, "target", "exception-unifier", "check-result.json"), context.arguments().get(0));
                Mockito.when(mock.exists())
                        .thenReturn(false);
            })) {
                Assertions.assertNull(resultHandler.getCheckResult());
                Assertions.assertEquals(1, fileMockedConstruction.constructed().size());
            }
        }
    }

    @Nested
    class StoreSyncResultTest {

        @Test
        void should_create_sync_result_successfully() {
            final MojoConfiguration mojoConfiguration = MojoConfigurationFixture.getMockMojoConfigurationWithBuildConsumer(
                    (build -> Mockito.when(build.getDirectory())
                            .thenReturn("target"))
            );
            final ResultHandler resultHandler = new ResultHandler(mojoConfiguration);
            final SyncResult syncResult = SyncResultFixture.getSyncResult();

            final ArgumentCaptor<String> writeFileContentArgumentCaptor = ArgumentCaptor.forClass(String.class);
            final ArgumentCaptor<String> writeFilePathArgumentCaptor = ArgumentCaptor.forClass(String.class);

            try (MockedStatic<FileUtil> fileUtilMockedStatic = Mockito.mockStatic(FileUtil.class)) {
                fileUtilMockedStatic
                        .when(() -> FileUtil.writeUtf8String(writeFileContentArgumentCaptor.capture(), writeFilePathArgumentCaptor.capture()))
                        .thenReturn(Mockito.mock(File.class));

                resultHandler.storeSyncResult(syncResult);

                Assertions.assertEquals("{\"moduleId\":\"com.test\",\"syncedExceptionSources\":[{\"source\":\"ABC\",\"syncedExceptionCodes\":[{\"name\":\"TEST\",\"code\":\"SAMPLE:ABC:001\",\"originalCode\":\"001\",\"message\":\"Not Found\"}]}]}", writeFileContentArgumentCaptor.getValue());
                Assertions.assertEquals(
                        String.join(File.separator, "target", "exception-unifier", "sync-result.json"),
                        writeFilePathArgumentCaptor.getValue()
                );
            }
        }
    }

    @Nested
    class GetSyncResultTest {

        @Test
        void should_get_sync_result_successfully_given_it_exists() {
            final ArgumentCaptor<File> fileArgumentCaptor = ArgumentCaptor.forClass(File.class);
            final SyncResult syncResult = Mockito.mock(SyncResult.class);

            try (final MockedStatic<FileUtil> fileUtilMockedStatic = Mockito.mockStatic(FileUtil.class);
                 final MockedStatic<JSONUtil> jsonUtilMockedStatic = Mockito.mockStatic(JSONUtil.class)) {

                jsonUtilMockedStatic.when(() -> JSONUtil.toBean(ArgumentMatchers.eq("mocked json"), ArgumentMatchers.eq(SyncResult.class)))
                        .thenReturn(syncResult);

                final ResultHandler resultHandler = new ResultHandler(MojoConfigurationFixture.getMockMojoConfigurationWithBuildConsumer(
                        (build) -> Mockito.when(build.getDirectory())
                                .thenReturn("target")
                ));

                try (final MockedConstruction<File> fileMockedConstruction = Mockito.mockConstruction(File.class, (mock, context) -> {
                    Assertions.assertEquals(String.join(File.separator, "target", "exception-unifier", "sync-result.json"), context.arguments().get(0));
                    Mockito.when(mock.exists())
                            .thenReturn(true);
                })) {
                    fileUtilMockedStatic.when(() -> FileUtil.readUtf8String(fileArgumentCaptor.capture()))
                            .thenReturn("mocked json");

                    Assertions.assertEquals(syncResult, resultHandler.getSyncResult());
                    Assertions.assertEquals(fileMockedConstruction.constructed().get(0), fileArgumentCaptor.getValue());
                }
            }
        }

        @Test
        void should_return_null_given_check_result_file_does_not_exist() {
            final ResultHandler resultHandler = new ResultHandler(MojoConfigurationFixture.getMockMojoConfigurationWithBuildConsumer(
                    (build) -> Mockito.when(build.getDirectory())
                            .thenReturn("target")
            ));

            try (final MockedConstruction<File> fileMockedConstruction = Mockito.mockConstruction(File.class, (mock, context) -> {
                Assertions.assertEquals(String.join(File.separator, "target", "exception-unifier", "sync-result.json"), context.arguments().get(0));
                Mockito.when(mock.exists())
                        .thenReturn(false);
            })) {
                Assertions.assertNull(resultHandler.getSyncResult());
                Assertions.assertEquals(1, fileMockedConstruction.constructed().size());
            }
        }
    }
}
