package pl.touk.sputnik.engine.visitor;

import org.junit.jupiter.api.Test;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationSetup;
import pl.touk.sputnik.review.Paths;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;
import pl.touk.sputnik.review.ReviewFormatterFactory;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FilterOutTestFilesVisitorTest {

    @Test
    void shouldFilterOutTestFiles() {
        Configuration config = new ConfigurationSetup().setUp(Collections.<String, String>emptyMap());
        Review review = new Review(Arrays.asList(createReviewFile(true), createReviewFile(false)), ReviewFormatterFactory.get(config));

        new FilterOutTestFilesVisitor(Paths.SRC_TEST).beforeReview(review);

        assertThat(review.getFiles()).hasSize(1);
    }

    private ReviewFile createReviewFile(boolean isTestFile) {
        return when(mock(ReviewFile.class).getReviewFilename()).thenReturn(isTestFile?Paths.SRC_TEST+"/somefile":"somefile").getMock();
    }

}