package pl.touk.sputnik.processor.codenarc;

import org.junit.Test;
import pl.touk.sputnik.review.*;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class CodeNarcProcessorTest {

    CodeNarcProcessor sut = new CodeNarcProcessor();

    @Test
    public void shouldReturnSomeViolationsForFile() {
        //given
        String reviewFilePath = "src/test/resources/codeNarcTestFiles/FileWithOneViolationLevel2.groovy";
        ReviewFile reviewFile = new ReviewFile(reviewFilePath);
        Review review = new Review(Arrays.asList(reviewFile));
        //when
        ReviewResult result = sut.process(review);
        //then
        assertThat(result).isNotNull();
        assertThat(result.getViolations()).isNotNull().isNotEmpty().hasSize(1).contains(new Violation(reviewFilePath, 5, "EmptyTryBlock: The try block is empty", Severity.WARNING));
    }

    @Test
    public void shouldReturnViolationsOfEachLevelForFile() {
        //given
        String reviewFilePath = "src/test/resources/codeNarcTestFiles/FileWithOneViolationPerEachLevel.groovy";
        ReviewFile reviewFile = new ReviewFile(reviewFilePath);
        Review review = new Review(Arrays.asList(reviewFile));
        //when
        ReviewResult result = sut.process(review);
        //then
        assertThat(result).isNotNull();
        assertThat(result.getViolations()).isNotNull().isNotEmpty().hasSize(3).containsSequence(
                new Violation(reviewFilePath, 5, "ForLoopShouldBeWhileLoop: The for loop can be simplified to a while loop", Severity.ERROR),
                new Violation(reviewFilePath, 9, "AssertWithinFinallyBlock: A finally block within class FileWithOneViolationPerEachLevel contains an assert statement, potentially hiding the original exception, if there is one", Severity.WARNING),
                new Violation(reviewFilePath, 13, "EmptyMethod: Violation in class FileWithOneViolationPerEachLevel. The method bar is both empty and not marked with @Override", Severity.INFO)
        );
    }

    @Test
    public void shouldReturnNoViolationsForPerfectFile() {
        //given
        String reviewFilePath = "src/test/resources/codeNarcTestFiles/FileWithoutViolations.groovy";
        ReviewFile reviewFile = new ReviewFile(reviewFilePath);
        Review review = new Review(Arrays.asList(reviewFile));
        //when
        ReviewResult result = sut.process(review);
        //then
        assertThat(result).isNotNull();
        assertThat(result.getViolations()).isNotNull().isEmpty();
    }

    @Test
    public void shouldReturnNoViolationsWhenNoFiles() {
        //given
        Review review = new Review(new ArrayList<ReviewFile>());
        //when
        ReviewResult result = sut.process(review);
        //then
        assertThat(result).isNotNull();
        assertThat(result.getViolations()).isNotNull().isEmpty();
    }
}