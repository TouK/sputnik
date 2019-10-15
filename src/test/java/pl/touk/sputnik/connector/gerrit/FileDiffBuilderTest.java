package pl.touk.sputnik.connector.gerrit;

import com.google.gerrit.extensions.common.DiffInfo;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileDiffBuilderTest {

    private static final String FILE_KEY = "test.java";

    @Test
    void shouldMoveWithHunkSkip() {
        List<DiffInfo.ContentEntry> content = new ArrayList<>();
        content.add(buildContentEntry(0, 0, 10));
        content.add(buildContentEntry(0, 2, 0));

        FileDiff fileDiff = new FileDiffBuilder().build(FILE_KEY, content);

        assertThat(fileDiff.getFileName()).isEqualTo(FILE_KEY);
        assertThat(fileDiff.getModifiedLines()).containsExactlyInAnyOrder(11, 12);
    }

    @Test
    void shouldNotMoveWithHunkA() {
        List<DiffInfo.ContentEntry> content = new ArrayList<>();
        content.add(buildContentEntry(10, 0, 0));
        content.add(buildContentEntry(0, 2, 0));

        FileDiff fileDiff = new FileDiffBuilder().build(FILE_KEY, content);

        assertThat(fileDiff.getFileName()).isEqualTo(FILE_KEY);
        assertThat(fileDiff.getModifiedLines()).containsExactlyInAnyOrder(1, 2);
    }

    @Test
    void shouldComputeComplexHunks() {
        List<DiffInfo.ContentEntry> content = new ArrayList<>();
        content.add(buildContentEntry(10, 5, 1));
        content.add(buildContentEntry(3, 2, 20));

        FileDiff fileDiff = new FileDiffBuilder().build(FILE_KEY, content);

        assertThat(fileDiff.getFileName()).isEqualTo(FILE_KEY);
        assertThat(fileDiff.getModifiedLines()).containsExactlyInAnyOrder(2, 3, 4, 5, 6, 27, 28);
    }

    /**
     * We build ContentEntry instance because it is a final class
     */
    private DiffInfo.ContentEntry buildContentEntry(int aSize, int bSize, int skip) {
        DiffInfo.ContentEntry diffHunk = new DiffInfo.ContentEntry();
        diffHunk.a = buildListMock(aSize);
        diffHunk.b = buildListMock(bSize);
        diffHunk.skip = skip;
        return diffHunk;
    }

    private List<String> buildListMock(int size) {
        List<String> list = mock(List.class);
        when(list.size()).thenReturn(size);
        return list;
    }


}