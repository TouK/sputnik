package pl.touk.sputnik.processor.eslint;

class ESLintException extends RuntimeException {

    ESLintException(String message, Exception cause) {
        super(message, cause);
    }
}
