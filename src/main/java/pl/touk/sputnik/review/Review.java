package pl.touk.sputnik.review;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.touk.sputnik.review.filter.FileFilter;
import pl.touk.sputnik.review.transformer.FileTransformer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
@Setter
@ToString
public class Review {
    private List<ReviewFile> files;

    /**
     * Report problems with configuration, processors and other.
     * There problems should be displayed on review summary with your code-review tool
     *
     */
    private List<String> problems = new ArrayList<>();

    /**
     * Messages that will be displayed on review summary with your code-review tool
     */
    private List<String> messages = new ArrayList<>();
    private Map<String, Short> scores = new HashMap<>();

    private final ReviewFormatter formatter;

    public Review(@NotNull List<ReviewFile> files, ReviewFormatter reviewFormatter) {
        this.files = files;
        this.formatter = reviewFormatter;
    }

    @NotNull
    public <T> List<T> getFiles(@NotNull FileFilter fileFilter, @NotNull FileTransformer<T> fileTransformer) {
        return fileTransformer.transform(fileFilter.filter(files));
    }

    @NotNull
    public List<String> getSourceDirs() {
        return Lists.transform(files, new ReviewFileSourceDirFunction());
    }

    public void addProblem(@NotNull String source, @NotNull String problem) {
        problems.add(formatter.formatProblem(source, problem));
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
                return;
            }
        }
        log.warn("Filename or Java class {} was not found in current review", violation.getFilenameOrJavaClassName());
    }

    private void addError(@NotNull ReviewFile reviewFile, @NotNull String source, int line, @Nullable String message, Severity severity) {
        reviewFile.getComments().add(new Comment(line, formatter.formatComment(source, severity, message), severity));
    }

    public long getTotalViolationCount() {
        return files.stream().map(ReviewFile::getComments)
                .mapToLong(Collection::size)
                .sum();
    }

    public long getViolationCount(Severity severity) {
        return files.stream().map(ReviewFile::getComments)
                .flatMap(Collection::stream)
                .filter(comment -> comment.getSeverity() == severity)
                .count();
    }

    @NoArgsConstructor
    private static class ReviewFileSourceDirFunction implements Function<ReviewFile, String> {

        @Override
        public String apply(ReviewFile from) {
            return from.getSourceDir();
        }
    }
}
