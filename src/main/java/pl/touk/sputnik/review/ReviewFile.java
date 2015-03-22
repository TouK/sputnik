package pl.touk.sputnik.review;

import lombok.Getter;
import lombok.ToString;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static pl.touk.sputnik.review.Paths.*;

@Getter
@ToString
public class ReviewFile {
    private static final Pattern ENTRY_PATTERN = Pattern.compile(ENTRY_REGEX);

    private final String reviewFilename;
    private final String javaClassName;
    private final File ioFile;
    private final List<Comment> comments = new ArrayList<>();

    public ReviewFile(@NotNull String reviewFilename) {
        this.reviewFilename = reviewFilename;
        this.javaClassName = createJavaClassName();
        this.ioFile = new File(reviewFilename);
    }

    @NotNull
    public String getSourceDir() {
        return StringUtils.substringBeforeLast(reviewFilename, JAVA).concat(JAVA);
    }

    @NotNull
    private String createJavaClassName() {
        return StringUtils.substringBeforeLast(ENTRY_PATTERN.matcher(reviewFilename).replaceFirst(""), DOT).replace('/', '.');
    }

    @NotNull
    public Boolean isSourceFile() {
        return StringUtils.contains(reviewFilename, SRC_MAIN);
    }

    @NotNull
    public Boolean isTestFile() {
        return StringUtils.contains(reviewFilename, SRC_TEST);
    }
}
