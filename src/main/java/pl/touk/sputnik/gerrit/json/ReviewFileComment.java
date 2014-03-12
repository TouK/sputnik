package pl.touk.sputnik.gerrit.json;

/**
 * Gerrit comment used with request for review input.
 * Used with JSON marshaller only.
 */
public class ReviewFileComment {
    public String message;

    public ReviewFileComment(String message) {
        this.message = message;
    }
}
