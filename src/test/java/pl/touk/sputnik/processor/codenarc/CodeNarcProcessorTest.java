package pl.touk.sputnik.processor.codenarc;

import org.junit.Test;
import pl.touk.sputnik.review.*;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class CodeNarcProcessorTest {

    @Test
    public void shouldReturnSomeViolationsForFile() {
        //given
        CodeNarcProcessor codeNarcProcessor = new CodeNarcProcessor();
        String reviewFilePath = "src/test/resources/codeNarcTestFiles/MyFile1.groovy";
        ReviewFile reviewFile = new ReviewFile(reviewFilePath);
        Review review = new Review(Arrays.asList(reviewFile));
        //when
        ReviewResult result = codeNarcProcessor.process(review);
        //then
        assertThat(result).isNotNull();
        assertThat(result.getViolations()).isNotNull().isNotEmpty().hasSize(1);
        Violation violation = result.getViolations().get(0);
        assertThat(violation).isNotNull();
        assertThat(violation.getLine()).isEqualTo(5);
        assertThat(violation.getMessage()).isEqualTo("The try block is empty");
        assertThat(violation.getSeverity()).isEqualTo(Severity.WARNING);
        assertThat(violation.getFilenameOrJavaClassName()).isEqualTo(reviewFilePath);
    }
}