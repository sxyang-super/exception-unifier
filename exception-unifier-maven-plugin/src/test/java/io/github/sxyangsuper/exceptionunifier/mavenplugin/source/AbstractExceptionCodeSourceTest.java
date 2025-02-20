package io.github.sxyangsuper.exceptionunifier.mavenplugin.source;

import io.github.sxyangsuper.exceptionunifier.base.Consts;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.MojoConfiguration;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.SyncResult;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class AbstractExceptionCodeSourceTest {

    @Nested
    class ValidateExceptionCodePrefixTest {

        AbstractExceptionCodeSource abstractExceptionCodeSource;

        @BeforeEach
        void setUp() {
            abstractExceptionCodeSource = new AbstractExceptionCodeSource() {
                @Override
                public void setupAndCheck(final MojoConfiguration mojoConfiguration) throws MojoExecutionException {

                }

                @Override
                public String getExceptionCodePrefix() throws MojoExecutionException {
                    return "";
                }

                @Override
                public void push(final SyncResult syncResult) throws MojoExecutionException {
                    
                }
            };
        }

        @Test
        void should_throw_exception_given_exception_code_prefix_is_blank() {
            final MojoExecutionException mojoExecutionException = Assertions.assertThrows(MojoExecutionException.class, () -> abstractExceptionCodeSource.validateExceptionCodePrefix("  "));
            Assertions.assertEquals("exception code prefix can not be blank.", mojoExecutionException.getMessage());
        }

        @Test
        void should_throw_exception_given_exception_code_prefix_contains_reserved_splitter() {
            final MojoExecutionException mojoExecutionException = Assertions.assertThrows(MojoExecutionException.class, () -> abstractExceptionCodeSource.validateExceptionCodePrefix(" " + Consts.EXCEPTION_CODE_SPLITTER + " "));
            Assertions.assertEquals(String.format("exception code prefix should not contain reserved splitter: %s", Consts.EXCEPTION_CODE_SPLITTER), mojoExecutionException.getMessage());
        }

        @Test
        void should_not_throw_exception_given_exception_code_prefix_is_valid() {
            Assertions.assertDoesNotThrow(() -> abstractExceptionCodeSource.validateExceptionCodePrefix("ABC"));
        }
    }
}