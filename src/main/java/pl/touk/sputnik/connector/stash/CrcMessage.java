package pl.touk.sputnik.connector.stash;

import pl.touk.sputnik.connector.gerrit.json.ReviewLineComment;

public class CrcMessage extends ReviewLineComment {
    public CrcMessage(ReviewLineComment comment) {
        super(comment.line, comment.message);
    }

    @Override
    public String getMessage() {
        return String.format("%s (%s)", message, line);
    }
}
