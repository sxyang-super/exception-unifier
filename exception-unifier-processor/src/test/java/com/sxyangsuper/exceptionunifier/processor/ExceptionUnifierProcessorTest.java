package com.sxyangsuper.exceptionunifier.processor;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.TraceClassVisitor;

import javax.tools.JavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;

import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static com.sxyangsuper.exceptionunifier.processor.Consts.PROPERTY_NAME_ANNOTATION_PROCESSOR_DEBUG;
import static com.sxyangsuper.exceptionunifier.processor.Consts.REMOTE_EXCEPTION_CODE_PARAMETER_NAME_MODULE_ID;
import static com.sxyangsuper.exceptionunifier.processor.Consts.REMOTE_EXCEPTION_CODE_PATH_GET_PREFIX;
import static com.sxyangsuper.exceptionunifier.processor.Consts.REMOTE_EXCEPTION_CODE_PATH_REPORT_EXCEPTION_ENUMS;
import static com.sxyangsuper.exceptionunifier.processor.TestUtil.getPackageCorrespondingTestDirectoryPath;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExceptionUnifierProcessorTest {

    JavaFileObject[] javaFileObjects;
    ExceptionUnifierProcessor exceptionUnifierProcessor;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        final String packageCorrespondingTestDirectoryPath = getPackageCorrespondingTestDirectoryPath(testInfo);
        final String testCompileSourceDirectoryPath = String.join(File.separator, System.getProperty("user.dir"), "src", "test", "compile", "source", packageCorrespondingTestDirectoryPath);
        final String packageName = packageCorrespondingTestDirectoryPath.replace(File.separator, ".");
        javaFileObjects = FileUtil.loopFiles(testCompileSourceDirectoryPath)
            .stream()
            .map(file -> {
                final String fullQualifiedName = String.join(".", packageName, file.getName().replace(".java", ""));
                final String source = FileUtil.readString(file, StandardCharsets.UTF_8);
                return JavaFileObjects.forSourceString(fullQualifiedName, source);
            })
            .toArray(JavaFileObject[]::new);

        exceptionUnifierProcessor = new ExceptionUnifierProcessor();

        System.setProperty(PROPERTY_NAME_ANNOTATION_PROCESSOR_DEBUG, "true");
    }

    @Test
    void should_do_nothing_given_no_exception_enums() {
        Compilation compilation = javac()
            .withProcessors(exceptionUnifierProcessor)
            .compile(javaFileObjects);

        assertThat(compilation).succeeded();
        assertThat(compilation).hadNoteContaining("Start annotation processing...");
        assertThat(compilation).hadNoteContaining("Start finding exception enums...");
        assertThat(compilation).hadNoteContaining("End finding exception enums, total count is: 0");
        assertThat(compilation).hadNoteContaining("Processing complete");
        assertThat(compilation).hadNoteCount(4);
    }

    /**
     * including no annotation case
     */
    @Test
    void should_not_log_debug_given_annotation_processor_debug_is_off() {
        System.setProperty(PROPERTY_NAME_ANNOTATION_PROCESSOR_DEBUG, "false");

        Compilation compilation = javac()
            .withProcessors(exceptionUnifierProcessor)
            .compile(javaFileObjects);

        assertThat(compilation).succeeded();
        assertThat(compilation).hadNoteContaining("Processing complete");
        assertThat(compilation).hadNoteCount(1);
    }

    @Test
    void should_throw_exception_given_exception_enum_does_not_have_exception_source_annotation() {
        final RuntimeException compileException = assertThrows(RuntimeException.class, () -> javac()
            .withProcessors(exceptionUnifierProcessor)
            .compile(javaFileObjects));


        final Throwable cause = compileException.getCause();
        assertInstanceOf(ExUnifierProcessException.class, cause);

        Assertions.assertEquals(
            "Invalid exception enum source.com.sxyangsuper.exceptionunifier.processor.ExceptionUnifierProcessorTest.should_throw_exception_given_exception_enum_does_not_have_exception_source_annotation.SampleExceptionEnum, not annotated with ExceptionSource",
            cause.getMessage()
        );
    }

    @Test
    void should_throw_exception_given_exception_source_value_is_blank() {
        final RuntimeException compileException = assertThrows(RuntimeException.class, () -> javac()
            .withProcessors(exceptionUnifierProcessor)
            .compile(javaFileObjects));


        final Throwable cause = compileException.getCause();
        assertInstanceOf(ExUnifierProcessException.class, cause);

        Assertions.assertEquals(
            "Invalid exception enum source.com.sxyangsuper.exceptionunifier.processor.ExceptionUnifierProcessorTest.should_throw_exception_given_exception_source_value_is_blank.SampleExceptionEnum, exception source value can not be blank",
            cause.getMessage()
        );
    }

    @Test
    void should_throw_exception_given_exception_source_value_contains_reserved_splitter() {
        final RuntimeException compileException = assertThrows(RuntimeException.class, () -> javac()
            .withProcessors(exceptionUnifierProcessor)
            .compile(javaFileObjects));


        final Throwable cause = compileException.getCause();
        assertInstanceOf(ExUnifierProcessException.class, cause);

        Assertions.assertEquals(
            "Invalid exception enum source.com.sxyangsuper.exceptionunifier.processor.ExceptionUnifierProcessorTest.should_throw_exception_given_exception_source_value_contains_reserved_splitter.SampleExceptionEnum, exception source value can not contain reserved character: \":\"",
            cause.getMessage()
        );
    }

    @Test
    void should_throw_exception_given_exception_code_value_is_blank() {
        final RuntimeException compileException = assertThrows(RuntimeException.class, () -> javac()
            .withProcessors(exceptionUnifierProcessor)
            .withOptions("-Xlint", String.format("-A%s=A", Consts.PROCESSOR_ARG_NAME_EXCEPTION_CODE_PREFIX))
            .compile(javaFileObjects));

        final Throwable cause = compileException.getCause();
        assertInstanceOf(ExUnifierProcessException.class, cause);

        Assertions.assertEquals(
            "Invalid exception enum source.com.sxyangsuper.exceptionunifier.processor.ExceptionUnifierProcessorTest.should_throw_exception_given_exception_code_value_is_blank.SampleExceptionEnum, contains enum with blank exception code",
            cause.getMessage()
        );
    }

    @Test
    void should_throw_exception_given_exception_code_value_contains_reserved_splitter() {
        final RuntimeException compileException = assertThrows(RuntimeException.class, () -> javac()
            .withProcessors(exceptionUnifierProcessor)
            .withOptions("-Xlint", String.format("-A%s=A", Consts.PROCESSOR_ARG_NAME_EXCEPTION_CODE_PREFIX))
            .compile(javaFileObjects));

        final Throwable cause = compileException.getCause();
        assertInstanceOf(ExUnifierProcessException.class, cause);

        Assertions.assertEquals(
            "Invalid exception enum source.com.sxyangsuper.exceptionunifier.processor.ExceptionUnifierProcessorTest.should_throw_exception_given_exception_code_value_contains_reserved_splitter.SampleExceptionEnum, contains enum has code A:B contains reserved character: \":\"",
            cause.getMessage()
        );
    }

    @Test
    void should_throw_exception_given_there_is_duplicate_code_in_one_enum() {
        final RuntimeException compileException = assertThrows(RuntimeException.class, () -> javac()
            .withProcessors(exceptionUnifierProcessor)
            .compile(javaFileObjects));


        final Throwable cause = compileException.getCause();
        assertInstanceOf(ExUnifierProcessException.class, cause);

        Assertions.assertEquals(
            "Duplicate exception code A:A",
            cause.getMessage()
        );
    }

    @Test
    void should_throw_exception_given_there_is_duplicate_code_in_different_enums() {
        final RuntimeException compileException = assertThrows(RuntimeException.class, () -> javac()
            .withProcessors(exceptionUnifierProcessor)
            .compile(javaFileObjects));


        final Throwable cause = compileException.getCause();
        assertInstanceOf(ExUnifierProcessException.class, cause);

        Assertions.assertEquals(
            "Duplicate exception code A:B",
            cause.getMessage()
        );
    }

    @Test
    void should_throw_exception_given_exception_code_prefix_is_not_set() {
        final RuntimeException compileException = assertThrows(RuntimeException.class, () -> javac()
            .withProcessors(exceptionUnifierProcessor)
            .compile(javaFileObjects));

        final Throwable cause = compileException.getCause();
        assertInstanceOf(ExUnifierProcessException.class, cause);

        Assertions.assertEquals(
            "Fail to get exception code prefix",
            cause.getMessage()
        );
    }

    @Test
    void should_throw_exception_given_exception_code_prefix_is_set_to_blank() {
        final RuntimeException compileException = assertThrows(RuntimeException.class, () -> javac()
            .withProcessors(exceptionUnifierProcessor)
            .withOptions("-Xlint", String.format("-A%s= ", Consts.PROCESSOR_ARG_NAME_EXCEPTION_CODE_PREFIX))
            .compile(javaFileObjects));


        final Throwable cause = compileException.getCause();
        assertInstanceOf(ExUnifierProcessException.class, cause);

        Assertions.assertEquals(
            "Fail to get exception code prefix",
            cause.getMessage()
        );
    }

    @Test
    void should_throw_exception_given_exception_code_prefix_contains_reserved_splitter() {
        final RuntimeException compileException = assertThrows(RuntimeException.class, () -> javac()
            .withProcessors(exceptionUnifierProcessor)
            .withOptions("-Xlint", String.format("-A%s=A:A", Consts.PROCESSOR_ARG_NAME_EXCEPTION_CODE_PREFIX))
            .compile(javaFileObjects));


        final Throwable cause = compileException.getCause();
        assertInstanceOf(ExUnifierProcessException.class, cause);

        Assertions.assertEquals(
            "Invalid exception code prefix A:A, can not contain reserved character: :",
            cause.getMessage()
        );
    }

    @Test
    void should_process_successfully() throws IOException {
        final Compilation compilation = javac()
            .withProcessors(exceptionUnifierProcessor)
            .withOptions("-Xlint", String.format("-A%s=A", Consts.PROCESSOR_ARG_NAME_EXCEPTION_CODE_PREFIX))
            .compile(javaFileObjects);


        assertThat(compilation).succeeded();
        assertThat(compilation).hadNoteCount(13);

        final JavaFileObject enumJavaFileObject = compilation.generatedFiles().toArray(new JavaFileObject[0])[1];
        String output = getJavaFileObjectGeneratedFilesSourceAsString(enumJavaFileObject);
        Assertions.assertTrue(output.contains("A:A:A"));
    }

    @Test
    void should_not_change_code_of_non_exception_enum_without_implementing_exception_enum_asserts() throws IOException {
        final Compilation compilation = javac()
            .withProcessors(exceptionUnifierProcessor)
            .withOptions("-Xlint", String.format("-A%s=A", Consts.PROCESSOR_ARG_NAME_EXCEPTION_CODE_PREFIX))
            .compile(javaFileObjects);

        assertThat(compilation).succeeded();

        final JavaFileObject enumJavaFileObject = compilation.generatedFiles().toArray(new JavaFileObject[0])[0];
        String output = getJavaFileObjectGeneratedFilesSourceAsString(enumJavaFileObject);
        Assertions.assertFalse(output.contains("A:A:A"));
    }

    @Test
    void should_not_change_code_of_non_exception_enum() throws IOException {
        final Compilation compilation = javac()
            .withProcessors(exceptionUnifierProcessor)
            .withOptions("-Xlint", String.format("-A%s=A", Consts.PROCESSOR_ARG_NAME_EXCEPTION_CODE_PREFIX))
            .compile(javaFileObjects);

        assertThat(compilation).succeeded();

        final JavaFileObject enumJavaFileObject = compilation.generatedFiles().toArray(new JavaFileObject[0])[0];
        String output = getJavaFileObjectGeneratedFilesSourceAsString(enumJavaFileObject);
        Assertions.assertFalse(output.contains("A:A:A"));
    }

    private String getJavaFileObjectGeneratedFilesSourceAsString(final JavaFileObject enumJavaFileObject) throws IOException {
        ClassReader classReader = new ClassReader(IoUtil.readBytes(enumJavaFileObject.openInputStream()));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(out);
        TraceClassVisitor traceClassVisitor = new TraceClassVisitor(printWriter);
        classReader.accept(traceClassVisitor, 0);

        printWriter.flush();
        printWriter.close();

        return out.toString();
    }

    @Nested
    @WireMockTest
    class RemoteExceptionCodePrefixSupplierTest {

        @Test
        void should_throw_exception_given_no_module_id() {
            final RuntimeException compileException = assertThrows(RuntimeException.class,
                () -> javac()
                    .withProcessors(exceptionUnifierProcessor)
                    .withOptions(
                        "-Xlint",
                        String.format("-A%s=http://localhost", Consts.PROCESSOR_ARG_NAME_REMOTE_BASE_URL),
                        String.format("-A%s=", Consts.PROCESSOR_ARG_NAME_MODULE_ID)
                    )
                    .compile(javaFileObjects)
            );

            final Throwable cause = compileException.getCause();
            assertInstanceOf(ExUnifierProcessException.class, cause);

            Assertions.assertEquals(
                String.format("Blank module id, please provide it with compile arg %s", Consts.PROCESSOR_ARG_NAME_MODULE_ID),
                cause.getMessage()
            );
        }

        @Test
        void should_throw_exception_given_get_exception_code_prefix_from_remote_fail(
            final WireMockRuntimeInfo wmRuntimeInfo
        ) {
            final String moduleId = "com.sample";

            stubFor(WireMock.get(String.format(
                    "%s?%s=%s",
                    REMOTE_EXCEPTION_CODE_PATH_GET_PREFIX,
                    REMOTE_EXCEPTION_CODE_PARAMETER_NAME_MODULE_ID,
                    moduleId
                ))
                .willReturn(WireMock.badRequest().withBody("This is a test error")));

            final RuntimeException compileException = assertThrows(RuntimeException.class,
                () -> javac()
                    .withProcessors(exceptionUnifierProcessor)
                    .withOptions(
                        "-Xlint",
                        String.format("-A%s=%s", Consts.PROCESSOR_ARG_NAME_REMOTE_BASE_URL, wmRuntimeInfo.getHttpBaseUrl()),
                        String.format("-A%s=%s", Consts.PROCESSOR_ARG_NAME_MODULE_ID, moduleId)
                    )
                    .compile(javaFileObjects)
            );

            final Throwable cause = compileException.getCause();
            assertInstanceOf(ExUnifierProcessException.class, cause);

            Assertions.assertEquals(
                String.format("Fail to get exception code prefix for module %s, response status is 400, message is This is a test error", moduleId),
                cause.getMessage()
            );
        }

        @ParameterizedTest(name = "should retry successfully given http response code is {arguments}")
        @CsvSource({
            "500",
            "502",
            "503",
            "504",
            "429",
        })
        void should_retry_successfully_given_get_exception_code_fail_with_specific_response_code(
            int httpResponseCode,
            final WireMockRuntimeInfo wmRuntimeInfo
        ) {
            final String moduleId = "com.sample";

            String requestURL = String.format(
                "%s?%s=%s",
                REMOTE_EXCEPTION_CODE_PATH_GET_PREFIX,
                REMOTE_EXCEPTION_CODE_PARAMETER_NAME_MODULE_ID,
                moduleId
            );
            stubFor(WireMock.get(requestURL)
                .willReturn(WireMock.status(httpResponseCode).withBody("This is a test error")));

            final RuntimeException compileException = assertThrows(RuntimeException.class,
                () -> javac()
                    .withProcessors(exceptionUnifierProcessor)
                    .withOptions(
                        "-Xlint",
                        String.format("-A%s=%s", Consts.PROCESSOR_ARG_NAME_REMOTE_BASE_URL, wmRuntimeInfo.getHttpBaseUrl()),
                        String.format("-A%s=%s", Consts.PROCESSOR_ARG_NAME_MODULE_ID, moduleId)
                    )
                    .compile(javaFileObjects)
            );

            final Throwable cause = compileException.getCause();
            assertInstanceOf(ExUnifierProcessException.class, cause);

            Assertions.assertEquals(
                String.format("Fail to get exception code prefix for module %s, remote request failed", moduleId),
                cause.getMessage()
            );

            verify(3, getRequestedFor(urlEqualTo(requestURL)));
        }

        @Test
        void should_throw_exception_given_get_exception_code_prefix_from_wrong_remote() {
            final RuntimeException compileException = assertThrows(RuntimeException.class,
                () -> javac()
                    .withProcessors(exceptionUnifierProcessor)
                    .withOptions(
                        "-Xlint",
                        String.format("-A%s=%s", Consts.PROCESSOR_ARG_NAME_REMOTE_BASE_URL, "http://localhost:9999"),
                        String.format("-A%s=%s", Consts.PROCESSOR_ARG_NAME_MODULE_ID, "com.sample")
                    )
                    .compile(javaFileObjects)
            );

            final Throwable cause = compileException.getCause();
            assertInstanceOf(ExUnifierProcessException.class, cause);

            Assertions.assertEquals(
                String.format("Fail to get exception code prefix for module %s, remote request failed", "com.sample"),
                cause.getMessage()
            );
        }

        @Test
        void should_process_successfully(
            final WireMockRuntimeInfo wmRuntimeInfo
        ) throws IOException {
            final String moduleId = "com.sample";
            final String mockedExceptionCodePrefix = "A";

            stubFor(WireMock.get(String.format(
                    "%s?%s=%s",
                    REMOTE_EXCEPTION_CODE_PATH_GET_PREFIX,
                    REMOTE_EXCEPTION_CODE_PARAMETER_NAME_MODULE_ID,
                    moduleId
                ))
                .willReturn(WireMock.ok(mockedExceptionCodePrefix)));

            final Compilation compilation = javac()
                .withProcessors(exceptionUnifierProcessor)
                .withOptions(
                    "-Xlint",
                    String.format("-A%s=%s", Consts.PROCESSOR_ARG_NAME_REMOTE_BASE_URL, wmRuntimeInfo.getHttpBaseUrl()),
                    String.format("-A%s=%s", Consts.PROCESSOR_ARG_NAME_MODULE_ID, moduleId)
                )
                .compile(javaFileObjects);

            assertThat(compilation).succeeded();
            assertThat(compilation).hadNoteCount(14);

            final JavaFileObject enumJavaFileObject = compilation.generatedFiles().toArray(new JavaFileObject[0])[0];
            String output = getJavaFileObjectGeneratedFilesSourceAsString(enumJavaFileObject);
            Assertions.assertTrue(output.contains("A:A:A"));
        }

        @Nested
        class CollectExceptionCodesTest {

            final String moduleId = "com.sample";
            final String mockedExceptionCodePrefix = "A";

            @BeforeEach
            void setUp() {
                stubFor(WireMock.get(String.format(
                        "%s?%s=%s",
                        REMOTE_EXCEPTION_CODE_PATH_GET_PREFIX,
                        REMOTE_EXCEPTION_CODE_PARAMETER_NAME_MODULE_ID,
                        moduleId
                    ))
                    .willReturn(WireMock.ok(mockedExceptionCodePrefix)));
            }

            @Test
            void should_log_warning_given_not_providing_corresponding_endpoint_from_server(
                final WireMockRuntimeInfo wmRuntimeInfo
            ) {
                final Compilation compilation = javac()
                    .withProcessors(exceptionUnifierProcessor)
                    .withOptions(
                        "-Xlint",
                        String.format("-A%s=%s", Consts.PROCESSOR_ARG_NAME_REMOTE_BASE_URL, wmRuntimeInfo.getHttpBaseUrl()),
                        String.format("-A%s=%s", Consts.PROCESSOR_ARG_NAME_MODULE_ID, moduleId)
                    )
                    .compile(javaFileObjects);

                assertThat(compilation).succeeded();
                assertThat(compilation).hadNoteContaining(
                    String.format("[WARNING] [EX UNIFIER] Not found endpoint %s/exception-enums/bulk to report exception enums",
                        wmRuntimeInfo.getHttpBaseUrl())
                );
            }

            @Test
            void should_throw_exception_given_report_exception_enums_fails(
                final WireMockRuntimeInfo wmRuntimeInfo
            ) {
                stubFor(WireMock.post(REMOTE_EXCEPTION_CODE_PATH_REPORT_EXCEPTION_ENUMS)
                    .willReturn(
                        WireMock.badRequest()
                            .withBody("Test fail reason")
                    ));

                final RuntimeException compileException = assertThrows(RuntimeException.class,
                    () -> javac()
                        .withProcessors(exceptionUnifierProcessor)
                        .withOptions(
                            "-Xlint",
                            String.format("-A%s=%s", Consts.PROCESSOR_ARG_NAME_REMOTE_BASE_URL, wmRuntimeInfo.getHttpBaseUrl()),
                            String.format("-A%s=%s", Consts.PROCESSOR_ARG_NAME_MODULE_ID, moduleId)
                        )
                        .compile(javaFileObjects)
                );

                final Throwable cause = compileException.getCause();
                assertInstanceOf(ExUnifierProcessException.class, cause);

                Assertions.assertEquals(
                    String.format(
                        "Fail to report exception enums, response status is %s, message is %s",
                        HttpStatus.HTTP_BAD_REQUEST,
                        "Test fail reason"
                    ),
                    cause.getMessage()
                );

                final ExceptionCodeReportMeta expectedCodeReportMeta = new ExceptionCodeReportMeta()
                    .setModuleId("com.sample")
                    .setPrefix("A")
                    .setMetaSources(Collections.singletonList(
                        new ExceptionCodeReportMeta.MetaSource()
                            .setSource("A")
                            .setExceptionCodes(Collections.singletonList(
                                new ExceptionCodeReportMeta.ExceptionCode()
                                    .setCode("A:A:A")
                                    .setMessagePlaceholder("This is test message {0}")
                            ))
                    ));
                verify(
                    postRequestedFor(
                        urlEqualTo(REMOTE_EXCEPTION_CODE_PATH_REPORT_EXCEPTION_ENUMS)
                    )
                        .withRequestBody(WireMock.equalToJson(JSONUtil.toJsonStr(expectedCodeReportMeta)))
                );
            }

            @Test
            void should_report_successfully(final WireMockRuntimeInfo wmRuntimeInfo) {
                stubFor(WireMock.post(REMOTE_EXCEPTION_CODE_PATH_REPORT_EXCEPTION_ENUMS)
                    .willReturn(
                        WireMock.created()
                    ));

                final Compilation compilation = javac()
                    .withProcessors(exceptionUnifierProcessor)
                    .withOptions(
                        "-Xlint",
                        String.format("-A%s=%s", Consts.PROCESSOR_ARG_NAME_REMOTE_BASE_URL, wmRuntimeInfo.getHttpBaseUrl()),
                        String.format("-A%s=%s", Consts.PROCESSOR_ARG_NAME_MODULE_ID, moduleId)
                    )
                    .compile(javaFileObjects);

                assertThat(compilation).succeeded();
                assertThat(compilation).hadNoteContaining("Successfully report 4 exception enums to server");

                final ExceptionCodeReportMeta expectedCodeReportMeta = new ExceptionCodeReportMeta()
                    .setModuleId("com.sample")
                    .setPrefix("A")
                    .setMetaSources(Arrays.asList(
                        new ExceptionCodeReportMeta.MetaSource()
                            .setSource("A")
                            .setExceptionCodes(Arrays.asList(
                                new ExceptionCodeReportMeta.ExceptionCode()
                                    .setCode("A:A:A")
                                    .setMessagePlaceholder("This is test message {0}"),
                                new ExceptionCodeReportMeta.ExceptionCode()
                                    .setCode("A:A:B")
                                    .setMessagePlaceholder("This is test b message {0}")
                            )),
                        new ExceptionCodeReportMeta.MetaSource()
                            .setSource("B")
                            .setExceptionCodes(Arrays.asList(
                                new ExceptionCodeReportMeta.ExceptionCode()
                                    .setCode("A:B:A")
                                    .setMessagePlaceholder("This is test message {0}"),
                                new ExceptionCodeReportMeta.ExceptionCode()
                                    .setCode("A:B:B")
                                    .setMessagePlaceholder("This is test b message {0}")
                            ))
                    ));
                verify(
                    postRequestedFor(
                        urlEqualTo(REMOTE_EXCEPTION_CODE_PATH_REPORT_EXCEPTION_ENUMS)
                    )
                        .withRequestBody(WireMock.equalToJson(JSONUtil.toJsonStr(expectedCodeReportMeta)))
                );
            }
        }
    }
}
