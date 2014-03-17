package pl.touk.sputnik.review;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.touk.sputnik.gerrit.json.ReviewFileComment;
import pl.touk.sputnik.gerrit.json.ReviewInput;
import pl.touk.sputnik.gerrit.json.ReviewLineComment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void addErrorOnAbsolutePath(@NotNull String fileName, @NotNull String source, int line, @NotNull String message, @NotNull Severity severity) {
        for (ReviewFile file : files) {
            if (file.getIoFile().getAbsolutePath().equals(fileName)) {
                addError(file, source, line, message, severity);
                return;
            }
        }
        LOG.warn("File name {} was not found in current review", fileName);
    }

    /**
     * @param javaClassName Java class name
     * @param source error source - e.g. Checkstyle
     * @param message message
     */
    public void addErrorOnJavaClassName(@NotNull String javaClassName, @NotNull String source, int line, @NotNull String message, @NotNull Severity severity) {
        for (ReviewFile file : files) {
            if (file.getJavaClassName().equals(javaClassName)) {
                addError(file, source, line, message, severity);
                return;
            }
        }
        LOG.warn("Java class {} was not found in current review", javaClassName);
    }

    private void addError(@NotNull ReviewFile reviewFile, @NotNull String source, int line, @Nullable String message, Severity severity) {
        reviewFile.getComments().add(new Comment(line, String.format(COMMENT_FORMAT, source, severity, message)));
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
    public List<String> getJavaClassNames() {
        return Lists.transform(files, new Review.ReviewFileJavaFileNameFunction());
    }

    @NotNull
    public Map<String, File> getIoFileToJavaClassNames() {
        Map<String, File> result = new HashMap<String, File>();
        for (ReviewFile file : files) {
            result.put(file.getJavaClassName(), file.getIoFile());
        }
        return result;
    }

    @NotNull
    public List<String> getBuildDirs() {
        return Lists.transform(files, new ReviewFileBuildDirFunction());
    }

    @NotNull
    public List<String> getSourceDirs() {
        return Lists.transform(files, new ReviewFileSourceDirFunction());
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

    private static class ReviewFileJavaFileNameFunction implements Function<ReviewFile, String> {
        ReviewFileJavaFileNameFunction() { }

        @Override
        public String apply(ReviewFile from) {
            return from.getJavaClassName();
        }
    }

    private static class ReviewFileBuildDirFunction implements Function<ReviewFile, String> {
        ReviewFileBuildDirFunction() { }

        @Override
        public String apply(ReviewFile from) {
            return from.getBuildDir();
        }
    }

    private static class ReviewFileSourceDirFunction implements Function<ReviewFile, String> {
        ReviewFileSourceDirFunction() { }

        @Override
        public String apply(ReviewFile from) {
            return from.getSourceDir();
        }
    }
}
