package pl.touk.sputnik.engine.visitor.comment;

import com.google.gerrit.extensions.api.changes.FileApi;
import com.google.gerrit.extensions.api.changes.RevisionApi;
import com.google.gerrit.extensions.restapi.RestApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.touk.sputnik.connector.gerrit.GerritException;
import pl.touk.sputnik.connector.gerrit.GerritFacade;
import pl.touk.sputnik.engine.diff.FileDiff;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class GerritFileDiffBuilderWrapper {

    private final GerritFacade gerritFacade;
    private final GerritFileDiffBuilder gerritFileDiffBuilder;

    @Nonnull
    public List<FileDiff> buildFileDiffs() {
        log.info("Query all file diffs to only include comments on modified lines");

        try {
            RevisionApi revision = gerritFacade.getRevision();
            List<FileDiff> fileDiffs = revision.files().keySet().stream()
                    .map(fileName -> this.buildFileDiff(gerritFileDiffBuilder, revision, fileName))
                    .collect(Collectors.toList());
            log.info("Query all file diffs finished");
            return fileDiffs;
        } catch (RestApiException e) {
            throw new GerritException("Error when retrieve modified lines", e);
        }
    }

    private FileDiff buildFileDiff(@Nonnull GerritFileDiffBuilder gerritFileDiffBuilder, @Nonnull RevisionApi revisionApi, @Nonnull String fileName) {
        try {
            log.info("Query file diff for {}", fileName);
            FileApi fileApi = revisionApi.file(fileName);
            return gerritFileDiffBuilder.build(fileName, fileApi.diff().content);
        } catch (RestApiException e) {
            throw new GerritException("Error when retrieve modified lines", e);
        }
    }
}
