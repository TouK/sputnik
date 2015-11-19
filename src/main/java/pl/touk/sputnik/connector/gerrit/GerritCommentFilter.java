package pl.touk.sputnik.connector.gerrit;

import java.util.HashMap;
import java.util.Map;

import com.google.gerrit.extensions.api.GerritApi;
import com.google.gerrit.extensions.api.changes.FileApi;
import com.google.gerrit.extensions.api.changes.RevisionApi;
import com.google.gerrit.extensions.common.DiffInfo.ContentEntry;
import com.google.gerrit.extensions.common.FileInfo;
import com.google.gerrit.extensions.restapi.RestApiException;

public class GerritCommentFilter implements CommentFilter {

    private final GerritApi gerritApi;

    private final GerritPatchset patchset;

    private Map<String, FileDiff> modifiedLines;

    public GerritCommentFilter(GerritApi gerritApi, GerritPatchset patchset) {
        this.gerritApi = gerritApi;
        this.patchset = patchset;
    }

    @Override
    public boolean filter(String filePath, int line) {
        FileDiff diff = modifiedLines == null ? null : modifiedLines.get(filePath);
        return diff != null && !diff.getModifiedLines().contains(line);
    }

    @Override
    public GerritCommentFilter init() {
        modifiedLines = new HashMap<>();
        try {
            RevisionApi revision = gerritApi.changes().id(patchset.getChangeId()).revision(patchset.getRevisionId());
            for (Map.Entry<String, FileInfo> file : revision.files().entrySet()) {
                FileDiff fileDiff = new FileDiff(file.getKey());
                FileApi fileApi = revision.file(file.getKey());
                int currentLine = 1;
                for (ContentEntry diffHunk : fileApi.diff().content) {
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
                modifiedLines.put(fileDiff.getFileName(), fileDiff);
            }
            return this;
        } catch (RestApiException e) {
            throw new GerritException("Error when retreive modified lines", e);
        }
    }
}
