package pl.touk.sputnik.connector.stash;

import pl.touk.sputnik.connector.gerrit.json.ReviewLineComment;

public class CrcMessage extends ReviewLineComment {
    public CrcMessage(ReviewLineComment comment) {
        super(comment.line, comment.message);
    }

    @Override
    public String getMessage() {
        return String.format("%s (%s)", message, getCrc());
    }

    public String getCrc() {
        return Integer.toHexString(Math.abs(String.format("%s %s", line, message).hashCode()) % 1000).toUpperCase();
    }
}
