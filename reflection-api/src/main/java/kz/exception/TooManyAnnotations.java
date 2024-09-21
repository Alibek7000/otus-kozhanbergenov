package kz.exception;

public class TooManyAnnotations extends Exception {
    public TooManyAnnotations() {
        super("Method has too many incompatible annotations!");
    }
}
