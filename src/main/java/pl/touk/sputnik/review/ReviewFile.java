package pl.touk.sputnik.review;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReviewFile {
    private static final String MAVEN_ENTRY_REGEX = ".*src/(main|test)/java/";
    private static final String JAVA = "java/";
    private static final String SRC_MAIN = "src/main/";
    private static final String BUILD_MAIN = "target/classes/";
    private static final String SRC_TEST = "src/test/";
    private static final String BUILD_TEST = "target/test-classes/";
    private static final String DOT = ".";

    private final String gerritFilename;
    private final File ioFile;
    private final List<Comment> comments = new ArrayList<Comment>();

    public ReviewFile(@NotNull String gerritFilename) {
        this.gerritFilename = gerritFilename;
        this.ioFile = new File(gerritFilename);
    }

    @NotNull
    public String getGerritFilename() {
        return gerritFilename;
    }

    @NotNull
    public File getIoFile() {
        return ioFile;
    }

    @NotNull
    public List<Comment> getComments() {
        return comments;
    }

    @NotNull
    public String getJavaClassName() {
        return StringUtils.substringBeforeLast(gerritFilename.replaceFirst(MAVEN_ENTRY_REGEX, ""), DOT).replace('/', '.');
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
}
