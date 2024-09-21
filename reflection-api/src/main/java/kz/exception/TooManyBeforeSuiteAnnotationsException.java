package kz.exception;

public class TooManyBeforeSuiteAnnotationsException extends Exception{
    public TooManyBeforeSuiteAnnotationsException() {
        super("Class has more than one @BeforeSuite annotations!");
    }
}
