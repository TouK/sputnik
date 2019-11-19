package pl.touk.sputnik.engine.visitor.comment;

import com.google.gerrit.extensions.common.DiffInfo.ContentEntry;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.engine.diff.FileDiff;

import java.util.List;

@Slf4j
public class GerritFileDiffBuilder {

    @NotNull
    public FileDiff build(@NotNull String fileKey, @NotNull List<ContentEntry> content) {
        FileDiff fileDiff = new FileDiff(fileKey);
        int currentLine = 1;
        for (ContentEntry diffHunk : content) {
            if (diffHunk.skip != null) {
                currentLine += diffHunk.skip;
            }
            if (diffHunk.ab != null) {
                currentLine += diffHunk.ab.size();
            }
            if (diffHunk.b != null) {
                fileDiff.addHunk(currentLine, diffHunk.b.size());
                currentLine += diffHunk.b.size();
            }
        }
        return fileDiff;
    }
}
