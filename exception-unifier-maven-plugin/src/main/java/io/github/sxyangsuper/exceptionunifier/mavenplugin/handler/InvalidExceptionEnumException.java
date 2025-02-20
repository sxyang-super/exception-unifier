package io.github.sxyangsuper.exceptionunifier.mavenplugin.handler;

public class InvalidExceptionEnumException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidExceptionEnumException(final String message) {
        super(message);
    }
}
