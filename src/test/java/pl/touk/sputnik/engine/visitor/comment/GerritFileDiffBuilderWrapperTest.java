package pl.touk.sputnik.engine.visitor.comment;

import com.google.gerrit.extensions.api.changes.FileApi;
import com.google.gerrit.extensions.api.changes.RevisionApi;
import com.google.gerrit.extensions.common.DiffInfo;
import com.google.gerrit.extensions.common.FileInfo;
import com.google.gerrit.extensions.restapi.RestApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.touk.sputnik.connector.gerrit.GerritFacade;
import pl.touk.sputnik.engine.diff.FileDiff;

import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GerritFileDiffBuilderWrapperTest {
    private static final String FILENAME = "src/Cat.java";


    @Mock
    private GerritFacade gerritFacade;
    @Mock
    private GerritFileDiffBuilder gerritFileDiffBuilder;
    @Mock
    private RevisionApi revisionApi;
    @Mock
    private FileInfo fileInfo;
    @Mock
    private FileApi fileApi;
    @Mock
    private FileDiff fileDiff;

    private DiffInfo.ContentEntry contentEntry = new DiffInfo.ContentEntry();
    private DiffInfo diffInfo = new DiffInfo();
    private GerritFileDiffBuilderWrapper wrapper;

    @BeforeEach
    void setUp() {
        wrapper = new GerritFileDiffBuilderWrapper(gerritFacade, gerritFileDiffBuilder);
    }

    @Test
    void shouldBuildFileDiffs() throws RestApiException {
        when(gerritFacade.getRevision()).thenReturn(revisionApi);
        when(revisionApi.files()).thenReturn(singletonMap(FILENAME, fileInfo));
        when(revisionApi.file(FILENAME)).thenReturn(fileApi);
        when(fileApi.diff()).thenReturn(diffInfo);
        List<DiffInfo.ContentEntry> content = singletonList(contentEntry);
        diffInfo.content = content;
        when(gerritFileDiffBuilder.build(FILENAME, content)).thenReturn(fileDiff);

        List<FileDiff> fileDiffs = wrapper.buildFileDiffs();

        assertThat(fileDiffs).containsExactly(fileDiff);
    }
}