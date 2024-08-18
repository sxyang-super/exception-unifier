package com.sxyangsuper.exceptionunifier.processor;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.TraceClassVisitor;

import javax.tools.JavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static com.sxyangsuper.exceptionunifier.processor.TestUtil.getPackageCorrespondingTestDirectoryPath;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExceptionUnifierProcessorTest {
    // create directory for each test case manually
    // create compile source file in corresponding directory
    // setup: set source directory for each test case
    // scan source directory, and extract meta for generating JavaFileObject
    // for happy path, check generated content
    // for unhappy path, check thrown exception

    JavaFileObject[] javaFileObjects;
    ExceptionUnifierProcessor exceptionUnifierProcessor;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        final String packageCorrespondingTestDirectoryPath = getPackageCorrespondingTestDirectoryPath(testInfo);
        final String testCompilesourceDirectoryPath = String.join(File.separator, System.getProperty("user.dir"), "src", "test", "compile", "source", packageCorrespondingTestDirectoryPath);
        final String packageName = packageCorrespondingTestDirectoryPath.replace(File.separator, ".");
        javaFileObjects = FileUtil.loopFiles(testCompilesourceDirectoryPath)
            .stream()
            .map(file -> {
                final String fullQualifiedName = String.join(".", packageName, file.getName().replace(".java", ""));
                final String source = FileUtil.readString(file, StandardCharsets.UTF_8);
                return JavaFileObjects.forSourceString(fullQualifiedName, source);
            })
            .toArray(JavaFileObject[]::new);

        exceptionUnifierProcessor = new ExceptionUnifierProcessor();

        System.setProperty("annotation.processor.debug", "true");
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

    @Test
    void should_not_log_debug_given_annotation_processor_debug_is_off() {
        System.setProperty("annotation.processor.debug", "false");

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
        assertThat(compilation).hadNoteCount(12);

        final JavaFileObject enumJavaFileObject = compilation.generatedFiles().toArray(new JavaFileObject[0])[1];
        String output = getJavaFileObjectGeneratedFilesSourceAsString(enumJavaFileObject);
        System.out.println(output);
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
}
