package pl.touk.sputnik.processor.sonar;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;

import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.Severity;
import pl.touk.sputnik.review.Violation;

public class SonarProcessorTest {

    @Test
    public void testFilterResults() {
        ReviewResult results = new ReviewResult();
        results.add(new Violation("src/t/f.cs", 0, "", Severity.ERROR));
        results.add(new Violation("src/t/f2.cs", 0, "", Severity.ERROR));
        results.add(new Violation("src/t/f3.cs", 0, "", Severity.ERROR));
        results.add(new Violation("src/t/f4.cs", 0, "", Severity.ERROR));

        ReviewFile r1 = new ReviewFile("src/t/f.cs");
        ReviewFile r2 = new ReviewFile("src/t/f2.cs");
        Review review = new Review(ImmutableList.of(r1, r2));

        ReviewResult filteredResults = new SonarProcessor().filterResults(results, review);
        assertThat(filteredResults.getViolations())
            .extracting("filenameOrJavaClassName")
            .containsExactly("src/t/f.cs", "src/t/f2.cs");
    }

    @Test
    public void testProcessor() {
        SonarProcessor processor = new SonarProcessor(new SonarRunnerBuilder() {
            public SonarRunner prepareRunner(Review review) {
                return new SonarRunner(null, null) {
                    public File run() throws IOException {
                        return new File(Resources.getResource("json/sonar-result.json").getFile());
                    }
                };
            }
        });
        ReviewFile r1 = new ReviewFile("src/module2/dir/file2.cs");
        Review review = new Review(ImmutableList.of(r1));
        ReviewResult result = processor.process(review);
        assertThat(result.getViolations()).hasSize(3);
    }
}