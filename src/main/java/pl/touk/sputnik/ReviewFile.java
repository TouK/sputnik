package pl.touk.sputnik;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReviewFile {
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
}
