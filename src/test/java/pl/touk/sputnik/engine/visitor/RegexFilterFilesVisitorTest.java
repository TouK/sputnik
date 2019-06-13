package pl.touk.sputnik.engine.visitor;

import org.junit.jupiter.api.Test;
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

class RegexFilterFilesVisitorTest {

    @Test
    void shouldFilterOutTestFiles() {
        Configuration config = new ConfigurationSetup().setUp(Collections.<String, String>emptyMap());
        Review review = new Review(Arrays.asList(
            createReviewFile("pom.xml"),
            createReviewFile("module1"),
            createReviewFile("module2/p"),
            createReviewFile("module1/p"),
            createReviewFile("module1/pom.xml"),
            createReviewFile("test/module1/pom.xml"),
            createReviewFile("module1/x")),
            ReviewFormatterFactory.get(config));

        new RegexFilterFilesVisitor("^module1/p.*").beforeReview(review);

        assertThat(review.getFiles()).hasSize(2);
        assertThat(review.getFiles()).extracting("reviewFilename").containsExactly("module1/p", "module1/pom.xml");
    }

    private ReviewFile createReviewFile(String fileName) {
        return when(mock(ReviewFile.class).getReviewFilename()).thenReturn(fileName).getMock();
    }
}
