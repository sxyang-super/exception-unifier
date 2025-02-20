package io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.fixture.SyncHandlerTest.SyncExceptionEnumsTest.should_throw_exception_given_read_class_file_fail;

import io.github.sxyangsuper.exceptionunifier.processor.ExceptionSource;
import lombok.Getter;

@Getter
@ExceptionSource("ABC")
public enum SampleExceptionEnum implements ISampleExceptionEnumAsserts {
    TEST("001", "Not found", "Any value");
    private final String code;
    private final String message;
    private final String anyField;

    SampleExceptionEnum(String code, String message, String anyField) {
        this.code = code;
        this.message = message;
        this.anyField = anyField;
        System.out.println(this.code);
    }
}
