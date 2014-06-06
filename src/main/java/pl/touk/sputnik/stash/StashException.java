package pl.touk.sputnik.stash;

public class StashException extends RuntimeException {
    public StashException(String message) {
        super(message);
    }

    public StashException(String message, Throwable cause) {
        super(message, cause);
    }
}
