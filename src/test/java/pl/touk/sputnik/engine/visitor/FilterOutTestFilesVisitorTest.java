package pl.touk.sputnik.engine.visitor;

import org.junit.Test;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationSetup;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;
import pl.touk.sputnik.review.ReviewFormatterFactory;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FilterOutTestFilesVisitorTest {

    @Test
    public void shouldFilterOutTestFiles() {
        Configuration config = new ConfigurationSetup().setUp(Collections.<String, String>emptyMap());
        Review review = new Review(Arrays.asList(createReviewFile(true), createReviewFile(false)), ReviewFormatterFactory.get(config));

        new FilterOutTestFilesVisitor().beforeReview(review);

        assertThat(review.getFiles()).hasSize(1);
    }

    private ReviewFile createReviewFile(boolean isTestFile) {
        return when(mock(ReviewFile.class).isTestFile()).thenReturn(isTestFile).getMock();
    }

}