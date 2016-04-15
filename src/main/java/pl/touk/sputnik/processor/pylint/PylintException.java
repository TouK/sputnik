package pl.touk.sputnik.processor.pylint;

public class PylintException extends RuntimeException {
    public PylintException(String message) {
        super(message);
    }

    public PylintException(String message, Throwable t) {
        super(message, t);
    }
}
