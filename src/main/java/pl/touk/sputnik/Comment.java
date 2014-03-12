package pl.touk.sputnik;

public class Comment {
    private final int line;
    private final String message;

    public Comment(int line, String message) {
        this.line = line;
        this.message = message;
    }

    public int getLine() {
        return line;
    }

    public String getMessage() {
        return message;
    }
}
