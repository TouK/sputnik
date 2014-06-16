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
import pl.touk.sputnik.review.ReviewFile;
import pl.touk.sputnik.review.ReviewResult;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class FindBugsProcessorTest {

    private final FindBugsProcessor fixture = new FindBugsProcessor();

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
    public void shouldReturnEmptyResultWhenFileNotFound() {
        //given
        Review review = new Review(ImmutableList.of(new ReviewFile("test")));

        //when
        ReviewResult reviewResult = fixture.process(review);

        //then
        assertThat(reviewResult).isNotNull();
        assert reviewResult != null;
        assertThat(reviewResult.getViolations()).isEmpty();
    }

    @Test
    @Ignore
    public void shouldReturnBasicViolationsOnEmptyClass() {
        //given
        Review review = new Review(ImmutableList.of(new ReviewFile(Resources.getResource("TestFile.java").getFile())));

        //when
        ReviewResult reviewResult = fixture.process(review);

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
