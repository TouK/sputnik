package pl.touk.sputnik.review;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.touk.sputnik.review.filter.FileFilter;
import pl.touk.sputnik.review.transformer.FileTransformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Slf4j
@Data
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
        return files.stream()
                .map(ReviewFile::getBuildDir)
                .collect(toList());
    }

    @NotNull
    public List<String> getSourceDirs() {
        return files.stream()
                .map(ReviewFile::getSourceDir).collect(toList());
    }

    public void add(@NotNull String source, @NotNull ReviewResult reviewResult) {
        reviewResult.getViolations().forEach(v -> addError(source, v));
    }

    public void addError(String source, Violation violation) {
        for (ReviewFile file : files) {
            if (file.getReviewFilename().equals(violation.getFilenameOrJavaClassName())
                    || file.getIoFile().getAbsolutePath().equals(violation.getFilenameOrJavaClassName())
                    || file.getJavaClassName().equals(violation.getFilenameOrJavaClassName())) {
                addError(file, source, violation.getLine(), violation.getMessage(), violation.getSeverity());
                totalViolationsCount++; // why not just reviewFile.getComments().size() instead of storing this?
                return;
            }
        }
        log.warn("Filename or Java class {} was not found in current review", violation.getFilenameOrJavaClassName());
    }

    private void addError(@NotNull ReviewFile reviewFile, @NotNull String source, int line, @Nullable String message, Severity severity) {
        reviewFile.getComments().add(new Comment(line, String.format(COMMENT_FORMAT, source, severity, message)));
    }

}
