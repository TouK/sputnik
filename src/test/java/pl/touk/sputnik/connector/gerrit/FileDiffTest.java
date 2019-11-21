package pl.touk.sputnik.connector.gerrit;

import org.junit.jupiter.api.Test;
import pl.touk.sputnik.engine.diff.FileDiff;

import static org.assertj.core.api.Assertions.assertThat;

class FileDiffTest {

    private static final String FILE_NAME = "Book.java";

    private FileDiff fileDiff = new FileDiff(FILE_NAME);

    @Test
    void shouldAddHunk() {
        fileDiff.addHunk(2, 5);

        assertThat(fileDiff.getModifiedLines()).containsExactlyInAnyOrder(2, 3, 4, 5, 6);
    }

    @Test
    void shouldAddManyHunks() {
        fileDiff.addHunk(1, 2);
        fileDiff.addHunk(100, 4);
        fileDiff.addHunk(50, 3);

        assertThat(fileDiff.getModifiedLines()).containsExactlyInAnyOrder(
                1, 2,
                50, 51, 52,
                100, 101, 102, 103);
    }

}