package pl.touk.sputnik.processor.findbugs;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pl.touk.sputnik.configuration.ConfigurationHolder;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewException;
import pl.touk.sputnik.review.ReviewFile;
import pl.touk.sputnik.review.ReviewResult;

import static com.googlecode.catchexception.CatchException.caughtException;
import static com.googlecode.catchexception.apis.CatchExceptionAssertJ.when;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class FindBugsProcessorTest {

    private final FindBugsProcessor findBugsProcessor = new FindBugsProcessor();

    @Mock
    private Review review;

    @Before
    public void setUp() throws Exception {
        ConfigurationHolder.initFromResource("test.properties");
    }

    @After
    public void tearDown() throws Exception {
        ConfigurationHolder.reset();
    }

    @Test
    public void shouldThrowWhenFileNotFound() {
        //given
        Review review = new Review(ImmutableList.of(new ReviewFile("test")));

        //when
        when(findBugsProcessor).process(review);

        //then
        assertThat(caughtException()).isInstanceOf(ReviewException.class);
    }

    @Test
    @Ignore
    public void shouldReturnBasicViolationsOnEmptyClass() {
        //given
        Review review = new Review(ImmutableList.of(new ReviewFile(Resources.getResource("TestFile.java").getFile())));

        //when
        ReviewResult reviewResult = findBugsProcessor.process(review);

        //then
        assertThat(reviewResult).isNotNull();
        assert reviewResult != null;
        assertThat(reviewResult.getViolations()).isNotEmpty();
        assertThat(reviewResult.getViolations().size()).isEqualTo(3);
        assertThat(reviewResult.getViolations())
                .extracting("message")
                .containsOnly(
                        "Missing package-info.java file.",
                        "Missing a Javadoc comment."
                );
    }

}
