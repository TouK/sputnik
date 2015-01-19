package pl.touk.sputnik.connector.gerrit;

public class GerritException extends RuntimeException {
    public GerritException(String message, Throwable cause) {
        super(message, cause);
    }
}
