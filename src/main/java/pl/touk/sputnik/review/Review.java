package pl.touk.sputnik.review;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.touk.sputnik.review.filter.FileFilter;
import pl.touk.sputnik.review.transformer.FileTransformer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
@Setter
public class Review {
    /* Source, severity, message, e.g. [Checkstyle] Info: This is bad */

    private static final String COMMENT_FORMAT = "[%s] %s: %s";
    private List<ReviewFile> files;
    private int totalViolationsCount = 0;
    private List<String> messages = new ArrayList<>();
    private Map<String, Integer> scores = new HashMap<>();

    public Review(@NotNull List<ReviewFile> files) {
        this.files = files;
    }

    @NotNull
    public <T> List<T> getFiles(@NotNull FileFilter fileFilter, @NotNull FileTransformer<T> fileTransformer) {
        return fileTransformer.transform(fileFilter.filter(files));
    }

    @NotNull
    public List<String> getBuildDirs() {
        return Lists.transform(files, new ReviewFileBuildDirFunction());
    }

    @NotNull
    public List<String> getSourceDirs() {
        return Lists.transform(files, new ReviewFileSourceDirFunction());
    }

    public void add(@NotNull String source, @NotNull ReviewResult reviewResult) {
        for (Violation violation : reviewResult.getViolations()) {
            addError(source, violation);
        }
    }

    public void addError(String source, Violation violation) {
        for (ReviewFile file : files) {
            if (file.getReviewFilename().equals(violation.getFilenameOrJavaClassName())
                    || file.getIoFile().getAbsolutePath().equals(violation.getFilenameOrJavaClassName())
                    || file.getJavaClassName().equals(violation.getFilenameOrJavaClassName())) {
                addError(file, source, violation.getLine(), violation.getMessage(), violation.getSeverity());
                totalViolationsCount++;
                return;
            }
        }
        log.warn("Filename or Java class {} was not found in current review", violation.getFilenameOrJavaClassName());
    }

    private void addError(@NotNull ReviewFile reviewFile, @NotNull String source, int line, @Nullable String message, Severity severity) {
        reviewFile.getComments().add(new Comment(line, String.format(COMMENT_FORMAT, source, severity, message)));
    }

    private static class ReviewFileBuildDirFunction implements Function<ReviewFile, String> {

        ReviewFileBuildDirFunction() {
        }

        @Override
        public String apply(ReviewFile from) {
            return from.getBuildDir();
        }
    }

    private static class ReviewFileSourceDirFunction implements Function<ReviewFile, String> {

        ReviewFileSourceDirFunction() {
        }

        @Override
        public String apply(ReviewFile from) {
            return from.getSourceDir();
        }
    }
}
