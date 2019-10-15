package pl.touk.sputnik.connector.gerrit;

import com.google.gerrit.extensions.api.GerritApi;
import com.google.gerrit.extensions.api.changes.FileApi;
import com.google.gerrit.extensions.api.changes.RevisionApi;
import com.google.gerrit.extensions.common.FileInfo;
import com.google.gerrit.extensions.restapi.RestApiException;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class GerritCommentFilter implements CommentFilter {

    private final GerritApi gerritApi;

    private final GerritPatchset patchset;

    private Map<String, FileDiff> modifiedLines;

    public GerritCommentFilter(GerritApi gerritApi, GerritPatchset patchset) {
        this.gerritApi = gerritApi;
        this.patchset = patchset;
    }

    @Override
    public boolean include(String filePath, int line) {
        FileDiff diff = modifiedLines == null ? null : modifiedLines.get(filePath);
        return diff != null && diff.getModifiedLines().contains(line);
    }

    @Override
    public void init() {
        log.info("Query all file diffs to only include comments on modified lines");
        modifiedLines = new HashMap<>();
        FileDiffBuilder fileDiffBuilder = new FileDiffBuilder();
        try {
            RevisionApi revision = gerritApi.changes()
                    .id(patchset.getChangeId())
                    .revision(patchset.getRevisionId());

            for (Map.Entry<String, FileInfo> file : revision.files().entrySet()) {
                log.info("Query file diff for {}", file.getKey());
                FileApi fileApi = revision.file(file.getKey());
                FileDiff fileDiff = fileDiffBuilder.build(file.getKey(), fileApi.diff().content);
                modifiedLines.put(fileDiff.getFileName(), fileDiff);
                log.info("Query file diff for {} finished", file.getKey());
            }
        } catch (RestApiException e) {
            throw new GerritException("Error when retrieve modified lines", e);
        }
        log.info("Query all file diffs finished");
    }
}
