package pl.touk.sputnik.processor.checkstyle;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;
import pl.touk.sputnik.review.ReviewResult;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CheckstyleProcessorTest {

    private final CheckstyleProcessor fixture = new CheckstyleProcessor();

    @Mock
    private Review review;

    @Before
    public void setUp() throws Exception {
        Configuration.instance().setConfigurationResource("test.properties");
        Configuration.instance().init();
    }

    @After
    public void tearDown() throws Exception {
        Configuration.instance().reset();
    }

    @Test
    public void shouldReturnNotFoundViolation() {
        //given
        Review review = new Review(ImmutableList.of(new ReviewFile("test")));

        //when
        ReviewResult reviewResult = fixture.process(review);

        //then
        assertThat(reviewResult).isNotNull();
        assert reviewResult != null;
        assertThat(reviewResult.getViolations()).isNotEmpty();
        assertThat(reviewResult.getViolations())
                .extracting("message")
                .containsOnly("File not found!");
    }

    @Test
    public void shouldReturnBasicSunViolationsOnSimpleClass() {
        //given
        when(review.getIOFiles()).thenReturn(ImmutableList.of(getResourceAsFile("TestFile.java")));

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

    private File getResourceAsFile(String resourceName) {
        return new File(Resources.getResource(resourceName).getFile());
    }

}
