package kz.exception;

public class TooManyAfterSuiteAnnotationsException extends Exception {
    public TooManyAfterSuiteAnnotationsException() {
        super("Class has more than one @AfterSuite annotations!");
    }
}
