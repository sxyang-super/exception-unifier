package io.github.sxyangsuper.exceptionunifier.mavenplugin.handler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ExceptionEnumVariableTest {

    @Test
    void should_construct_exception_enum_successfully() {
        ExceptionEnumVariable exceptionEnumVariable = new ExceptionEnumVariable();

        exceptionEnumVariable.setCode("test code");
        exceptionEnumVariable.setMessage("test message");
        exceptionEnumVariable.setName("test name");

        Assertions.assertEquals("test code", exceptionEnumVariable.getCode());
        Assertions.assertEquals("test message", exceptionEnumVariable.getMessage());
        Assertions.assertEquals("test name", exceptionEnumVariable.getName());
    }
}
