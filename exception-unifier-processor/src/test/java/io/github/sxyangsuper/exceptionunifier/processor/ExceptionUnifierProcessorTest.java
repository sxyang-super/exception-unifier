package io.github.sxyangsuper.exceptionunifier.processor;

import cn.hutool.core.io.FileUtil;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import javax.tools.JavaFileObject;
import java.io.File;
import java.nio.charset.StandardCharsets;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static io.github.sxyangsuper.exceptionunifier.processor.Consts.PROPERTY_NAME_ANNOTATION_PROCESSOR_DEBUG;
import static io.github.sxyangsuper.exceptionunifier.processor.TestUtil.getPackageCorrespondingTestDirectoryPath;
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
    void should_throw_exception_given_exception_enum_not_implementing_any_interfaces() {
        final RuntimeException compileException = assertThrows(RuntimeException.class, () -> javac()
            .withProcessors(exceptionUnifierProcessor)
            .compile(javaFileObjects));

        final Throwable cause = compileException.getCause();
        assertInstanceOf(ExUnifierProcessException.class, cause);

        Assertions.assertEquals(
            "Invalid exception enum source.io.github.sxyangsuper.exceptionunifier.processor.ExceptionUnifierProcessorTest.should_throw_exception_given_exception_enum_not_implementing_any_interfaces.SampleExceptionEnum, exception enum mast be the descendant of interface: \"io.github.sxyangsuper.exceptionunifier.base.IExceptionEnumAsserts\"",
            cause.getMessage()
        );
    }

    @Test
    void should_throw_exception_given_exception_enum_is_not_descendant_of_exception_asserts() {
        final RuntimeException compileException = assertThrows(RuntimeException.class, () -> javac()
            .withProcessors(exceptionUnifierProcessor)
            .compile(javaFileObjects));

        final Throwable cause = compileException.getCause();
        assertInstanceOf(ExUnifierProcessException.class, cause);

        Assertions.assertEquals(
            "Invalid exception enum source.io.github.sxyangsuper.exceptionunifier.processor.ExceptionUnifierProcessorTest.should_throw_exception_given_exception_enum_is_not_descendant_of_exception_asserts.SampleExceptionEnum, exception enum mast be the descendant of interface: \"io.github.sxyangsuper.exceptionunifier.base.IExceptionEnumAsserts\"",
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
            "Invalid exception enum source.io.github.sxyangsuper.exceptionunifier.processor.ExceptionUnifierProcessorTest.should_throw_exception_given_exception_source_value_is_blank.SampleExceptionEnum, exception source value can not be blank",
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
            "Invalid exception enum source.io.github.sxyangsuper.exceptionunifier.processor.ExceptionUnifierProcessorTest.should_throw_exception_given_exception_source_value_contains_reserved_splitter.SampleExceptionEnum, exception source value can not contain reserved character: \":\"",
            cause.getMessage()
        );
    }

    @Test
    void should_process_successfully() {
        final Compilation compilation = javac()
            .withProcessors(exceptionUnifierProcessor)
            .compile(javaFileObjects);

        assertThat(compilation).succeeded();
        assertThat(compilation).hadNoteCount(6);
    }
}
