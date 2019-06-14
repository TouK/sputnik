package pl.touk.sputnik.processor.sonar;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
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

class SonarProcessorTest extends TestEnvironment {

    @Test
    void shouldFilterResultFiles() {
        //given
        ReviewResult results = new ReviewResult();
        results.add(new Violation("src/t/f.cs", 0, "", Severity.ERROR));
        results.add(new Violation("src/t/f2.cs", 0, "", Severity.ERROR));
        results.add(new Violation("src/t/f3.cs", 0, "", Severity.ERROR));
        results.add(new Violation("src/t/f4.cs", 0, "", Severity.ERROR));

        ReviewFile r1 = new ReviewFile("src/t/f.cs");
        ReviewFile r2 = new ReviewFile("src/t/f2.cs");
        Review review = new Review(ImmutableList.of(r1, r2), ReviewFormatterFactory.get(config));

        //when
        ReviewResult filteredResults = new SonarProcessor(config).filterResults(results, review);

        //then
        assertThat(filteredResults.getViolations())
            .extracting("filenameOrJavaClassName")
            .containsExactly("src/t/f.cs", "src/t/f2.cs");
    }

    @Test
    void shouldReportViolationsForMultiModulesProject() {
        //given
        SonarProcessor processor = new SonarProcessor(new SonarScannerBuilder() {
            public SonarScanner prepareRunner(Review review, Configuration configuration) {
                return new SonarScanner(null, null, null) {
                    public File run() throws IOException {
                        return getResourceAsFile("json/sonar-result-mutli-module.json");
                    }
                };
            }
        }, config);

        //when
        ReviewResult result = processor.process(nonExistentReview("src/module2/dir/file2.cs"));

        //then
        assertThat(result.getViolations()).hasSize(3);
    }

    @Test
    void shouldReportViolationsForSingleModulesProject() {
        //given
        SonarProcessor processor = new SonarProcessor(new SonarScannerBuilder() {
            public SonarScanner prepareRunner(Review review, Configuration configuration) {
                return new SonarScanner(null, null, null) {
                    public File run() throws IOException {
                        return getResourceAsFile("json/sonar-result-single-module.json");
                    }
                };
            }
        }, config);

        //when
        ReviewResult result = processor.process(nonExistentReview("src/dir/file2.cs"));

        //then
        assertThat(result.getViolations()).hasSize(3);
    }
}