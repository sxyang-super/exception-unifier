package io.github.sxyangsuper.exceptionunifier.mavenplugin.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.CharsetUtil;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.fixture.MojoConfigurationFixture.getMockMojoConfiguration;

class CheckHandlerTest {

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
    class CheckExceptionEnumsTest {

        @Test
        void should_skip_non_class_file() throws MojoExecutionException {
            String tempNoClassFilePath = String.join(File.separator, TEST_CASE_CORRESPONDING_CLASS_DIRECTORY, "application.yml");
            FileUtil.writeString("", tempNoClassFilePath, CharsetUtil.CHARSET_UTF_8);
            CheckHandler checkHandler = new CheckHandler(getMockMojoConfiguration(TEST_CASE_CORRESPONDING_CLASS_DIRECTORY));
            List<ExceptionEnum> exceptionEnums = checkHandler.checkExceptionEnums()
                .getExceptionEnums();
            Assertions.assertTrue(CollUtil.isEmpty(exceptionEnums));
            FileUtil.del(tempNoClassFilePath);
        }

        @Test
        void should_succeed_given_all_classes_are_not_exception_enum() throws MojoExecutionException {
            CheckHandler checkHandler = new CheckHandler(getMockMojoConfiguration(TEST_CASE_CORRESPONDING_CLASS_DIRECTORY));
            List<ExceptionEnum> exceptionEnums = checkHandler.checkExceptionEnums()
                    .getExceptionEnums();
            Assertions.assertTrue(CollUtil.isEmpty(exceptionEnums));
        }

        @Test
        void should_succeed_given_no_duplicate_code_within_all_exception_enums() throws MojoExecutionException {
            CheckHandler checkHandler = new CheckHandler(getMockMojoConfiguration(TEST_CASE_CORRESPONDING_CLASS_DIRECTORY));
            List<ExceptionEnum> exceptionEnums = checkHandler.checkExceptionEnums()
                    .getExceptionEnums();
            Assertions.assertEquals(2, CollUtil.size(exceptionEnums));
            Assertions.assertEquals(String.join(File.separator, TEST_CASE_CORRESPONDING_CLASS_DIRECTORY, "SampleExceptionEnum.class"), exceptionEnums.get(0).getClassFilePath());
            Assertions.assertEquals(5, exceptionEnums.get(0).getTotalConstructorParameterCount());
            Assertions.assertEquals(3, exceptionEnums.get(0).getCodeConstructorParametersIndex());
            Assertions.assertEquals(4, exceptionEnums.get(0).getMessageConstructorParametersIndex());


            Assertions.assertEquals(String.join(File.separator, TEST_CASE_CORRESPONDING_CLASS_DIRECTORY, "SampleExceptionEnum2.class"), exceptionEnums.get(1).getClassFilePath());
            Assertions.assertEquals(4, exceptionEnums.get(1).getTotalConstructorParameterCount());
            Assertions.assertEquals(3, exceptionEnums.get(1).getCodeConstructorParametersIndex());
            Assertions.assertEquals(4, exceptionEnums.get(1).getMessageConstructorParametersIndex());
        }

        @Test
        void should_throw_exception_given_there_is_duplication_in_same_exception_enum() {
            CheckHandler checkHandler = new CheckHandler(getMockMojoConfiguration(TEST_CASE_CORRESPONDING_CLASS_DIRECTORY));
            MojoExecutionException mojoExecutionException = Assertions.assertThrows(MojoExecutionException.class, checkHandler::checkExceptionEnums);
            Assertions.assertEquals("Duplicate exception code ABC:001 within io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.fixture.CheckHandlerTest.CheckExceptionEnumsTest.should_throw_exception_given_there_is_duplication_in_same_exception_enum.SampleExceptionEnum", mojoExecutionException.getMessage());
        }

        @Test
        void should_throw_exception_given_there_is_duplication_between_different_exception_enums() {
            CheckHandler checkHandler = new CheckHandler(getMockMojoConfiguration(TEST_CASE_CORRESPONDING_CLASS_DIRECTORY));
            MojoExecutionException mojoExecutionException = Assertions.assertThrows(MojoExecutionException.class, checkHandler::checkExceptionEnums);
            Assertions.assertEquals("Duplicate exception code ABC:001 between io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.fixture.CheckHandlerTest.CheckExceptionEnumsTest.should_throw_exception_given_there_is_duplication_between_different_exception_enums.SampleExceptionEnum2 and io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.fixture.CheckHandlerTest.CheckExceptionEnumsTest.should_throw_exception_given_there_is_duplication_between_different_exception_enums.SampleExceptionEnum", mojoExecutionException.getMessage());
        }

        @Test
        void should_throw_exception_given_read_class_file_fail() throws IOException {
            File file = new File(String.join(File.separator, TEST_CASE_CORRESPONDING_CLASS_DIRECTORY, "NotExceptionEnum.class"));
            try (final MockedStatic<FileUtil> fileUtilMockedStatic = Mockito.mockStatic(FileUtil.class)) {
                fileUtilMockedStatic.when(() -> FileUtil.loopFiles(ArgumentMatchers.anyString()))
                        .then((invocation -> {
                            if (!invocation.getMethod().getName().equals("getInputStream")) {
                                return ListUtil.of(file);
                            }

                            throw new IORuntimeException("mocked exception");
                        }));

                CheckHandler checkHandler = new CheckHandler(getMockMojoConfiguration(TEST_CASE_CORRESPONDING_CLASS_DIRECTORY));
                MojoExecutionException mojoExecutionException = Assertions.assertThrows(MojoExecutionException.class, checkHandler::checkExceptionEnums);
                Assertions.assertEquals("Fail to get input stream of file " + file.getAbsolutePath(), mojoExecutionException.getMessage());
            }
        }

        @Test
        void should_throw_exception_given_exception_enum_constructor_implementation_is_illegal() {
            CheckHandler checkHandler = new CheckHandler(getMockMojoConfiguration(TEST_CASE_CORRESPONDING_CLASS_DIRECTORY));
            final IllegalConstructorImplementationException illegalConstructorImplementationException = Assertions.assertThrows(IllegalConstructorImplementationException.class, checkHandler::checkExceptionEnums);
            final String expectedExceptionMessage = "Illegal constructor for class io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.fixture.CheckHandlerTest.CheckExceptionEnumsTest.should_throw_exception_given_exception_enum_constructor_implementation_is_illegal.SampleExceptionEnum, code value should be one of constructor argument";
            Assertions.assertEquals(expectedExceptionMessage, illegalConstructorImplementationException.getMessage());
        }

        @Test
        void should_throw_exception_given_exception_enum_code_value_is_not_set_by_constructor() {
            CheckHandler checkHandler = new CheckHandler(getMockMojoConfiguration(TEST_CASE_CORRESPONDING_CLASS_DIRECTORY));
            final InvalidExceptionEnumException invalidExceptionEnumException = Assertions.assertThrows(InvalidExceptionEnumException.class, checkHandler::checkExceptionEnums);
            Assertions.assertEquals("Invalid exception enum, value of reserved field code should be set by constructor", invalidExceptionEnumException.getMessage());
        }

        @Test
        void should_throw_exception_given_exception_enum_message_value_is_not_set_by_constructor() {
            CheckHandler checkHandler = new CheckHandler(getMockMojoConfiguration(TEST_CASE_CORRESPONDING_CLASS_DIRECTORY));
            final InvalidExceptionEnumException invalidExceptionEnumException = Assertions.assertThrows(InvalidExceptionEnumException.class, checkHandler::checkExceptionEnums);
            Assertions.assertEquals("Invalid exception enum, value of reserved field message should be set by constructor", invalidExceptionEnumException.getMessage());
        }
    }
}
