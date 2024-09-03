package com.sxyangsuper.exceptionunifier.processor;

class ExUnifierProcessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ExUnifierProcessException(final String message) {
        super(message);
    }

    public ExUnifierProcessException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
