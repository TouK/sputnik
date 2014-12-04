package pl.touk.sputnik.processor.findbugs;

import com.google.common.collect.ImmutableList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import pl.touk.sputnik.configuration.ConfigurationHolder;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewException;
import pl.touk.sputnik.review.ReviewFile;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.filter.FileFilter;
import pl.touk.sputnik.review.transformer.ClassNameTransformer;

import java.util.List;

import static com.googlecode.catchexception.CatchException.caughtException;
import static com.googlecode.catchexception.apis.CatchExceptionAssertJ.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;

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
//    @Ignore
    public void shouldReturnBasicViolationsOnEmptyClass() {
        //given
        List<String> file = ImmutableList.of("toreview.TestClass");
        Mockito.when(review.getFiles(any(FileFilter.class), any(ClassNameTransformer.class))).thenReturn(file);
        Mockito.when(review.getBuildDirs()).thenReturn(ImmutableList.of("build/classes/test"));
        Mockito.when(review.getSourceDirs()).thenReturn(ImmutableList.of("src/test/java"));

        //when
        ReviewResult reviewResult = findBugsProcessor.process(review);

        //then
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

}
