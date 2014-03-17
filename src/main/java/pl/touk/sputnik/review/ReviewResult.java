package pl.touk.sputnik.review;

import org.jetbrains.annotations.NotNull;

public class ReviewResult {

    /**
     * @param fileName physical file name with full path
     * @param source error source - e.g. Checkstyle
     * @param message message
     */
    public void addError(@NotNull String fileName, @NotNull String source, int line, @NotNull String message, @NotNull Severity severity) {
        for (ReviewFile file : files) {
            if (file.getIoFile().getAbsolutePath().equals(fileName)) {
                file.getComments().add(new Comment(line, String.format(COMMENT_FORMAT, source, severity, message)));
                return;
            }
        }
        LOG.warn("File name {} was not found in current review", fileName);
    }
}
