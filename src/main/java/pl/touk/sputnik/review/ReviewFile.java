package pl.touk.sputnik.review;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Getter
public class ReviewFile {
    private static final String MAVEN_ENTRY_REGEX = ".*src/(main|test)/java/";
    private static final String JAVA = "java/";
    private static final String SRC_MAIN = "src/main/";
    private static final String BUILD_MAIN = "target/classes/";
    private static final String SRC_TEST = "src/test/";
    private static final String BUILD_TEST = "target/test-classes/";
    private static final String DOT = ".";
    private static final Pattern MAVEN_ENTRY_PATTERN = Pattern.compile(MAVEN_ENTRY_REGEX);

    private final String gerritFilename;
    private final String javaClassName;
    private final File ioFile;
    private final List<Comment> comments = new ArrayList<Comment>();

    public ReviewFile(@NotNull String gerritFilename) {
        this.gerritFilename = gerritFilename;
        this.javaClassName = createJavaClassName();
        this.ioFile = new File(gerritFilename);
    }

    @NotNull
    public String getSourceDir() {
        return StringUtils.substringBeforeLast(gerritFilename, JAVA).concat(JAVA);
    }

    @NotNull
    public String getBuildDir() {
        if (StringUtils.contains(gerritFilename, SRC_MAIN)) {
            return StringUtils.substringBeforeLast(gerritFilename, SRC_MAIN).concat(BUILD_MAIN);
        }
        if (StringUtils.contains(gerritFilename, SRC_TEST)) {
            return StringUtils.substringBeforeLast(gerritFilename, SRC_TEST).concat(BUILD_TEST);
        }
        return getSourceDir();
    }

    @NotNull
    private String createJavaClassName() {
        return StringUtils.substringBeforeLast(MAVEN_ENTRY_PATTERN.matcher(gerritFilename).replaceFirst(""), DOT).replace('/', '.');
    }
}
