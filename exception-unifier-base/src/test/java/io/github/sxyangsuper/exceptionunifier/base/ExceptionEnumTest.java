package io.github.sxyangsuper.exceptionunifier.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ExceptionEnumTest {

    @Test
    void should_get_correct_exception_source() {
        final IExceptionEnum exceptionEnum = new IExceptionEnum() {
            @Override
            public String getCode() {
                return String.join(Consts.EXCEPTION_CODE_SPLITTER, "SAMPLE", "USER", "001");
            }

            @Override
            public String getMessage() {
                return "";
            }
        };

        Assertions.assertEquals("USER", exceptionEnum.getExceptionSource());
    }
}
