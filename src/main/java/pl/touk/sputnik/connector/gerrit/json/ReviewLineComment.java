package pl.touk.sputnik.connector.gerrit.json;

public class ReviewLineComment extends ReviewFileComment {
    public Integer line;

    public ReviewLineComment(Integer line, String message) {
        super(message);
        this.line = line;
    }
}
