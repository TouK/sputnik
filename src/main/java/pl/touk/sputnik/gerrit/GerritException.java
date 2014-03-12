package pl.touk.sputnik.gerrit;

public class GerritException extends RuntimeException {
    public GerritException(String message) {
        super(message);
    }

    public GerritException(String message, Throwable cause) {
        super(message, cause);
    }
}
