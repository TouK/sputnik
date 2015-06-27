package pl.touk.sputnik.processor.sonar;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import pl.touk.sputnik.TestEnvironment;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;
import pl.touk.sputnik.review.ReviewFormatterFactory;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.Severity;
import pl.touk.sputnik.review.Violation;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class SonarProcessorTest extends TestEnvironment {

    @Test
    public void shouldFilterResultFiles() {
        ReviewResult results = new ReviewResult();
        results.add(new Violation("src/t/f.cs", 0, "", Severity.ERROR));
        results.add(new Violation("src/t/f2.cs", 0, "", Severity.ERROR));
        results.add(new Violation("src/t/f3.cs", 0, "", Severity.ERROR));
        results.add(new Violation("src/t/f4.cs", 0, "", Severity.ERROR));

        ReviewFile r1 = new ReviewFile("src/t/f.cs");
        ReviewFile r2 = new ReviewFile("src/t/f2.cs");
        Review review = new Review(ImmutableList.of(r1, r2), ReviewFormatterFactory.get(config));

        ReviewResult filteredResults = new SonarProcessor(config).filterResults(results, review);
        assertThat(filteredResults.getViolations())
            .extracting("filenameOrJavaClassName")
            .containsExactly("src/t/f.cs", "src/t/f2.cs");
    }

    @Test
    public void shouldReportViolations() {
        SonarProcessor processor = new SonarProcessor(new SonarRunnerBuilder() {
            public SonarRunner prepareRunner(Review review, Configuration configuration) {
                return new SonarRunner(null, null, null) {
                    public File run() throws IOException {
                        return getResourceAsFile("json/sonar-result.json");
                    }
                };
            }
        }, config);
        ReviewResult result = processor.process(nonexistantReview("src/module2/dir/file2.cs"));
        assertThat(result.getViolations()).hasSize(3);
    }
}