package com.sxyangsuper.exceptionunifier.processor;

import java.util.List;

public interface IExceptionCodePrefixSupplier {
    String get();
    default void collectExceptionCodes(@SuppressWarnings("unused") final List<ExceptionCodeExpressions> exceptionCodeExpressionsList) {
        // do nothing by default
    }
    String getModuleId();
}
