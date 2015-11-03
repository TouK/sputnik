package pl.touk.sputnik.connector.gerrit;


public interface CommentFilter {

    public static final CommentFilter EMPTY_FILTER = new CommentFilter() {

        @Override
        public boolean filter(String filePath, int line) {
            return false;
        }
    };

    boolean filter(String filePath, int line);
}
