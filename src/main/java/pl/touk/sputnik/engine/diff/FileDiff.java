package pl.touk.sputnik.engine.diff;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FileDiff {

    private final String fileName;

    private final Set<Integer> modifiedLines = new HashSet<>();

    public FileDiff(String fileName) {
        this.fileName = fileName;
    }

    public void addHunk(int firstLine, int count) {
        for (int i = firstLine; i < firstLine + count; i++) {
            modifiedLines.add(i);
        }
    }

    public String getFileName() {
        return fileName;
    }

    public Set<Integer> getModifiedLines() {
        return Collections.unmodifiableSet(modifiedLines);
    }
}
