package pl.touk.sputnik.processor.findbugs;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pl.touk.sputnik.TestEnvironment;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewException;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.filter.FileFilter;
import pl.touk.sputnik.review.transformer.ClassNameTransformer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static pl.touk.sputnik.CatchException.catchException;

@RunWith(MockitoJUnitRunner.class)
public class FindBugsProcessorTest extends TestEnvironment {

    private final FindBugsProcessor findBugsProcessor = new FindBugsProcessor();

    @Mock
    private Review review;

    @Test
    public void shouldReturnBasicViolationsOnEmptyClass() {
        //given
        List<String> file = ImmutableList.of("toreview.TestClass");
        when(review.getFiles(any(FileFilter.class), any(ClassNameTransformer.class))).thenReturn(file);
        when(review.getBuildDirs()).thenReturn(ImmutableList.of("build/classes/test"));
        when(review.getSourceDirs()).thenReturn(ImmutableList.of("src/test/java"));

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

    @Test
    public void shouldThrowWhenFileNotFound() {
        //when
        catchException(() -> findBugsProcessor.process(nonexistantReview()), (caughtException) ->

        //then
        assertThat(caughtException).isInstanceOf(ReviewException.class));
    }

}
