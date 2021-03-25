package pl.touk.sputnik.processor.shellcheck;

class ShellcheckException extends RuntimeException {
    ShellcheckException(String message) {
        super(message);
    }

    ShellcheckException(String message, Throwable t) {
        super(message, t);
    }
}
