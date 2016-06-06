package pl.touk.sputnik.processor.pylint;

class PylintException extends RuntimeException {
    PylintException(String message) {
        super(message);
    }

    PylintException(String message, Throwable t) {
        super(message, t);
    }
}
