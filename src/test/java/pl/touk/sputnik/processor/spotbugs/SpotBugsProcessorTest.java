package pl.touk.sputnik.processor.spotbugs;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import pl.touk.sputnik.TestEnvironment;
import pl.touk.sputnik.configuration.ConfigurationSetup;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;
import pl.touk.sputnik.review.ReviewFormatterFactory;
import pl.touk.sputnik.review.ReviewResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SpotBugsProcessorTest extends TestEnvironment {

    private static final String GRADLE = "gradle";

    private SpotBugsProcessor spotBugsProcessor;

    @Before
    public void setUp() {
        config = new ConfigurationSetup().setUp(ImmutableMap.of(GeneralOption.BUILD_TOOL.getKey(), GRADLE));
        spotBugsProcessor = new SpotBugsProcessor(config);
    }

    @Test
    public void shouldReturnBasicViolationsOnEmptyClass() {
        List<ReviewFile> files = ImmutableList.of(new ReviewFile("src/test/java/toreview/TestClass.java"));
        Review review = new Review(files, ReviewFormatterFactory.get(config));

        ReviewResult reviewResult = spotBugsProcessor.process(review);

        assertThat(reviewResult).isNotNull();
        assertThat(reviewResult.getViolations())
                .isNotEmpty()
                .hasSize(2)
                .extracting("message")
                .containsOnly(
                        "DLS: Dead store to value in toreview.TestClass.incorrectAssignmentInIfCondition()",
                        "QBA: toreview.TestClass.incorrectAssignmentInIfCondition() assigns boolean literal in boolean expression"
                );
    }

    @Test
    public void shouldReturnEmptyWhenNoFilesToReview() {
        ReviewResult reviewResult = spotBugsProcessor.process(nonexistantReview());

        assertThat(reviewResult).isNotNull();
        assertThat(reviewResult.getViolations()).isEmpty();
    }

}
