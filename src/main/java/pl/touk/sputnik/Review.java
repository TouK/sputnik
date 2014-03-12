package pl.touk.sputnik;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.touk.sputnik.gerrit.json.ReviewFileComment;
import pl.touk.sputnik.gerrit.json.ReviewInput;
import pl.touk.sputnik.gerrit.json.ReviewLineComment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Review {
    private static final Logger LOG = LoggerFactory.getLogger(Review.class);
    /* Source, severity, message, e.g. [Checkstyle] Info: This is bad */
    private static final String COMMENT_FORMAT = "[%s] %s: %s";
    private final List<ReviewFile> files;

    public Review(List<ReviewFile> files) {
        this.files = files;
    }

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

    @NotNull
    public List<File> getIOFiles() {
        return Lists.transform(files, new Review.ReviewFileFileFunction());
    }

    @NotNull
    public List<String> getIOFilenames() {
        return Lists.transform(files, new Review.ReviewFileFilenameFunction());
    }

    @NotNull
    public ReviewInput toReviewInput() {
        ReviewInput reviewInput = new ReviewInput();
        reviewInput.setLabelToPlusOne();
        for (ReviewFile file : files) {
            List<ReviewFileComment> comments = new ArrayList<ReviewFileComment>();
            for (Comment comment : file.getComments()) {
                comments.add(new ReviewLineComment(comment.getLine(), comment.getMessage()));
            }
            reviewInput.comments.put(file.getGerritFilename(), comments);
        }

        return reviewInput;
    }

    private static class ReviewFileFileFunction implements Function<ReviewFile, File> {
        ReviewFileFileFunction() { }

        @Override
        public File apply(ReviewFile from) {
            return from.getIoFile();
        }
    }

    private static class ReviewFileFilenameFunction implements Function<ReviewFile, String> {
        ReviewFileFilenameFunction() { }

        @Override
        public String apply(ReviewFile from) {
            return from.getGerritFilename();
        }
    }
}
