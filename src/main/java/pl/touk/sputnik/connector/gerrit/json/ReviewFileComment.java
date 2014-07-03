package pl.touk.sputnik.connector.gerrit.json;

import lombok.Getter;
import lombok.ToString;

/**
 * Gerrit comment used with request for review input.
 * Used with JSON marshaller only.
 */
@ToString
@Getter
public class ReviewFileComment {
    public String message;

    public ReviewFileComment(String message) {
        this.message = message;
    }
}
