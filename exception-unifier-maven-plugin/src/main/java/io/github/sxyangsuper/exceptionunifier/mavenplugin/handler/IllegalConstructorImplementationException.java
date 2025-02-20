package io.github.sxyangsuper.exceptionunifier.mavenplugin.handler;

public class IllegalConstructorImplementationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public IllegalConstructorImplementationException(final String message) {
        super(message);
    }
}
