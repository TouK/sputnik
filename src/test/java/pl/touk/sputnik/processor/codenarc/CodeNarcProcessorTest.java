package pl.touk.sputnik.processor.codenarc;

import org.junit.Test;
import pl.touk.sputnik.review.*;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CodeNarcProcessorTest {

    CodeNarcProcessor sut = new CodeNarcProcessor();

    String reviewFileWithOneViolationPath = "src/test/resources/codeNarcTestFiles/FileWithOneViolationLevel2.groovy";
    String reviewFileWithViolationOnEachLevelPath = "src/test/resources/codeNarcTestFiles/FileWithOneViolationPerEachLevel.groovy";
    String reviewFileWithoutViolationPath = "src/test/resources/codeNarcTestFiles/FileWithoutViolations.groovy";

    @Test
    public void shouldReturnSomeViolationsForFile() {
        //given
        Review review = getReview(reviewFileWithOneViolationPath);
        //when
        ReviewResult result = sut.process(review);
        //then
        assertThat(result).isNotNull();
        assertThat(result.getViolations())
                .hasSize(1)
                .contains(new Violation(reviewFileWithOneViolationPath, 5, "EmptyTryBlock: The try block is empty", Severity.WARNING));
    }

    @Test
    public void shouldReturnViolationsOfEachLevelForFile() {
        //given
        Review review = getReview(reviewFileWithViolationOnEachLevelPath);
        //when
        ReviewResult result = sut.process(review);
        //then
        assertThat(result).isNotNull();
        assertThat(result.getViolations())
                .hasSize(3)
                .containsSequence(
                        new Violation(reviewFileWithViolationOnEachLevelPath, 5, "ForLoopShouldBeWhileLoop: The for loop can be simplified to a while loop", Severity.ERROR),
                        new Violation(reviewFileWithViolationOnEachLevelPath, 9, "AssertWithinFinallyBlock: A finally block within class FileWithOneViolationPerEachLevel contains an assert statement, potentially hiding the original exception, if there is one", Severity.WARNING),
                        new Violation(reviewFileWithViolationOnEachLevelPath, 13, "EmptyMethod: Violation in class FileWithOneViolationPerEachLevel. The method bar is both empty and not marked with @Override", Severity.INFO)
                );
    }

    @Test
    public void shouldReturnNoViolationsForPerfectFile() {
        //given
        ReviewFile reviewFile = new ReviewFile(reviewFileWithoutViolationPath);
        Review review = getReview(reviewFileWithoutViolationPath);
        //when
        ReviewResult result = sut.process(review);
        //then
        assertThat(result).isNotNull();
        assertThat(result.getViolations()).isNotNull().isEmpty();
    }

    @Test
    public void shouldReturnNoViolationsWhenNoFiles() {
        //given
        Review review = getReview();
        //when
        ReviewResult result = sut.process(review);
        //then
        assertThat(result).isNotNull();
        assertThat(result.getViolations()).isNotNull().isEmpty();
    }

    @Test
    public void shouldReturnViolationsFromManyFiles() {
        //given
        Review review = getReview(reviewFileWithOneViolationPath, reviewFileWithoutViolationPath, reviewFileWithViolationOnEachLevelPath);
        //when
        ReviewResult result = sut.process(review);
        //then
        assertThat(result).isNotNull();
        assertThat(result.getViolations())
                .hasSize(4)
                .containsSequence(
                        new Violation(reviewFileWithOneViolationPath, 5, "EmptyTryBlock: The try block is empty", Severity.WARNING),
                        new Violation(reviewFileWithViolationOnEachLevelPath, 5, "ForLoopShouldBeWhileLoop: The for loop can be simplified to a while loop", Severity.ERROR),
                        new Violation(reviewFileWithViolationOnEachLevelPath, 9, "AssertWithinFinallyBlock: A finally block within class FileWithOneViolationPerEachLevel contains an assert statement, potentially hiding the original exception, if there is one", Severity.WARNING),
                        new Violation(reviewFileWithViolationOnEachLevelPath, 13, "EmptyMethod: Violation in class FileWithOneViolationPerEachLevel. The method bar is both empty and not marked with @Override", Severity.INFO)
                );
    }

    private Review getReview(String... filePaths) {
        List<ReviewFile> files = new ArrayList<>();
        for (String filePath : filePaths) {
            files.add(new ReviewFile(filePath));
        }
        return new Review(files);
    }
}