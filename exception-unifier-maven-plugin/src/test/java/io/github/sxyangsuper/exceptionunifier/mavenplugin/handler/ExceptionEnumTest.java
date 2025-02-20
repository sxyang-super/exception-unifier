package io.github.sxyangsuper.exceptionunifier.mavenplugin.handler;

import cn.hutool.core.collection.ListUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

class ExceptionEnumTest {
    @Test
    void should_construct_exception_enum_successfully() {
        ExceptionEnum exceptionEnum = new ExceptionEnum();

        exceptionEnum.setExceptionEnumVariables(ListUtil.empty());
        exceptionEnum.setQualifiedClassName("test qualified class name");
        exceptionEnum.setSource("test source");

        Assertions.assertEquals(0, exceptionEnum.getExceptionEnumVariables().size());
        //noinspection DataFlowIssue
        Assertions.assertThrows(UnsupportedOperationException.class, () -> exceptionEnum.getExceptionEnumVariables().add(null));

        Assertions.assertEquals("test qualified class name", exceptionEnum.getQualifiedClassName());
        Assertions.assertEquals("test source", exceptionEnum.getSource());
        Assertions.assertEquals(Objects.hashCode("test qualified class name"), exceptionEnum.hashCode());
    }

    @Nested
    class EqualsTest {

        @Test
        void should_return_true_given_target_is_same_object() {
            ExceptionEnum exceptionEnum = new ExceptionEnum();
            AtomicReference<ExceptionEnum> exceptionEnumRef = new AtomicReference<>(exceptionEnum);
            Assertions.assertEquals(exceptionEnumRef.get(), exceptionEnum);
        }

        @Test
        void should_return_false_given_target_is_null() {
            Assertions.assertNotEquals(new ExceptionEnum(), null);
        }

        @Test
        void should_return_false_given_target_is_instance_of_its_sub_class() {
            Assertions.assertNotEquals(new ExceptionEnum(), new ExceptionEnum() {
            });
        }

        @Test
        void should_return_true_given_target_has_same_qualified_class_name() {
            ExceptionEnum source = new ExceptionEnum();
            source.setQualifiedClassName("test qualified class name");
            ExceptionEnum target = new ExceptionEnum();
            target.setQualifiedClassName("test qualified class name");
            Assertions.assertEquals(source, target);
        }

        @Test
        void should_return_false_given_target_has_different_qualified_class_name() {
            ExceptionEnum source = new ExceptionEnum();
            source.setQualifiedClassName("test qualified class name");
            ExceptionEnum target = new ExceptionEnum();
            target.setQualifiedClassName("another test qualified class name");
            Assertions.assertNotEquals(source, target);
        }
    }
}
