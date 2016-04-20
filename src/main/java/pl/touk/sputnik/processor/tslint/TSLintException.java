package pl.touk.sputnik.processor.tslint;

public class TSLintException extends RuntimeException {
    public TSLintException(String message) {
        super(message);
    }

    public TSLintException(String message, Throwable t) {
        super(message, t);
    }
}
