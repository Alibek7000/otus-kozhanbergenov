package kz.exceptions;

public class ApplicationInitializationException extends RuntimeException {
    public ApplicationInitializationException(String message) {
        super(message);
    }
}
