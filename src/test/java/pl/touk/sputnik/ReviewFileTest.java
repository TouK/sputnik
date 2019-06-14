package pl.touk.sputnik;

import org.junit.jupiter.api.Test;
import pl.touk.sputnik.review.ReviewFile;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewFileTest {
    private static final String MAIN_FILENAME = "gerrit-server/src/main/java/com/google/gerrit/server/project/RefControl.java";
    private static final String MAIN_JAVA_CLASS_NAME = "com.google.gerrit.server.project.RefControl";
    private static final String MAIN_JAVA_SOURCE_DIR = "gerrit-server/src/main/java/";

    private static final String TEST_FILENAME = "gerrit-server/src/test/java/com/google/gerrit/server/project/RefControlTest.java";
    private static final String TEST_JAVA_CLASS_NAME = "com.google.gerrit.server.project.RefControlTest";
    private static final String TEST_JAVA_SOURCE_DIR = "gerrit-server/src/test/java/";

    @Test
    void shouldReturnMainJavaClassName() {
        ReviewFile reviewFile = createReviewFile(MAIN_FILENAME);

        //expect
        assertThat(reviewFile.getJavaClassName()).isEqualTo(MAIN_JAVA_CLASS_NAME);
    }

    @Test
    void shouldReturnMainJavaSourceDirectory() {
        ReviewFile reviewFile = createReviewFile(MAIN_FILENAME);

        //expect
        assertThat(reviewFile.getSourceDir()).isEqualTo(MAIN_JAVA_SOURCE_DIR);
    }

    @Test
    void shouldReturnTestJavaClassName() {
        ReviewFile reviewFile = createReviewFile(TEST_FILENAME);

        //expect
        assertThat(reviewFile.getJavaClassName()).isEqualTo(TEST_JAVA_CLASS_NAME);
    }

    @Test
    void shouldReturnTestJavaSourceDirectory() {
        ReviewFile reviewFile = createReviewFile(TEST_FILENAME);

        //expect
        assertThat(reviewFile.getSourceDir()).isEqualTo(TEST_JAVA_SOURCE_DIR);
    }

    private ReviewFile createReviewFile(String reviewFileName) {
        return new ReviewFile(reviewFileName);
    }
}
