package kz.exceptions;

public class ApplicationInitializationException extends RuntimeException {
    public ApplicationInitializationException(String message) {
        super(message);
    }

    public ApplicationInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
