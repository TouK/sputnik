package pl.touk.sputnik.connector.gerrit;

public interface CommentFilter {

    public static final CommentFilter EMPTY_COMMENT_FILTER = new CommentFilter() {

        @Override
        public boolean include(String filePath, int line) {
            return true;
        }

        @Override
        public void init() {
        }
    };

    boolean include(String filePath, int line);

    void init();
}
