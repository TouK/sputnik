package pl.touk.sputnik.engine.diff;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
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
}
