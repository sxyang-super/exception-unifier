package com.sxyangsuper.exceptionunifier.processor;

import javax.annotation.processing.Messager;
import java.util.function.Supplier;

import static com.sxyangsuper.exceptionunifier.processor.Consts.PROPERTY_DEFAULT_VALUE_ANNOTATION_PROCESSOR_DEBUG;
import static com.sxyangsuper.exceptionunifier.processor.Consts.PROPERTY_NAME_ANNOTATION_PROCESSOR_DEBUG;
import static javax.tools.Diagnostic.Kind.NOTE;

public class Logger {
    private final Messager messager;
    private final boolean isDebugEnabled;

    public Logger(final Messager messager) {
        this.messager = messager;
        isDebugEnabled = Boolean.parseBoolean(
            System.getProperty(
                PROPERTY_NAME_ANNOTATION_PROCESSOR_DEBUG,
                PROPERTY_DEFAULT_VALUE_ANNOTATION_PROCESSOR_DEBUG
            )
        );


    }

    /* default */
    void debug(final Supplier<String> messageSupplier) {
        if (this.isDebugEnabled) {
            this.messager.printMessage(NOTE, String.format("[DEBUG] %s %s", Consts.LOG_PREFIX, messageSupplier.get()));
        }
    }

    /* default */
    void note(final String message) {
        this.messager.printMessage(NOTE, String.format("[NOTE] %s %s", Consts.LOG_PREFIX, message));
    }

    /* default */
    void warn(final String message) {
        this.messager.printMessage(NOTE, String.format("[WARNING] %s %s", Consts.LOG_PREFIX, message));
    }
}
