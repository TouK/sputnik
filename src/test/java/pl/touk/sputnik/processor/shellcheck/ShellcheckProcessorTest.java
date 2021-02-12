package pl.touk.sputnik.processor.shellcheck;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import pl.touk.sputnik.TestEnvironment;
import pl.touk.sputnik.configuration.ConfigurationBuilder;
import pl.touk.sputnik.exec.ExternalProcess;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;
import pl.touk.sputnik.review.ReviewFormatterFactory;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.Severity;
import pl.touk.sputnik.review.Violation;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ShellcheckProcessorTest extends TestEnvironment {

    private static final String CONFIGURATION_WITH_SHELLCHECK_ENABLED = "shellcheck/sputnik/noConfigurationFile.properties";
    private static final String REVIEW_FILE_WITH_ONE_VIOLATION = "src/test/resources/shellcheck/testFiles/oneViolation.sh";
    private static final String REVIEW_FILE_WITH_EXCLUDED_VIOLATION = "src/test/resources/shellcheck/testFiles/excludedViolation.sh";
    private static final String REVIEW_FILE_WITH_MULTIPLE_VIOLATIONS = "src/test/resources/shellcheck/testFiles/multipleViolations.sh";
    private ShellcheckProcessor sut;

    @BeforeEach
    public void setUp() {
        config = ConfigurationBuilder.initFromResource(CONFIGURATION_WITH_SHELLCHECK_ENABLED);
        sut = new ShellcheckProcessor(config);
    }

    @Test
    public void shouldReturnNoViolationsWhenThereIsNoFileToReview() {
        ReviewResult reviewResult = sut.process(nonExistentReview());

        assertThat(reviewResult).isNotNull();
        assertThat(reviewResult.getViolations()).isEmpty();
    }

    @Test
    public void shouldReturnOneViolationsForFile() {
        Assumptions.assumeTrue(isShellcheckInstalled());
        Review review = getReview(REVIEW_FILE_WITH_ONE_VIOLATION);

        ReviewResult result = sut.process(review);

        assertThat(result).isNotNull();
        assertThat(result.getViolations())
                .hasSize(1);
        Violation violation = result.getViolations().get(0);
        assertThat(violation.getFilenameOrJavaClassName()).contains(REVIEW_FILE_WITH_ONE_VIOLATION);
        assertThat(violation.getLine()).isEqualTo(3);
        assertThat(violation.getMessage()).isEqualTo("[Code:2154] Message: variable is referenced but not assigned.");
        assertThat(violation.getSeverity()).isEqualTo(Severity.WARNING);
    }

    @Test
    public void shouldReturnMultipleViolationsForFile() {
        Assumptions.assumeTrue(isShellcheckInstalled());
        Review review = getReview(REVIEW_FILE_WITH_MULTIPLE_VIOLATIONS);

        ReviewResult result = sut.process(review);

        assertThat(result).isNotNull();
        assertThat(result.getViolations())
                .hasSize(3);
        Violation violation1 = result.getViolations().get(0);
        assertThat(violation1.getFilenameOrJavaClassName()).contains(REVIEW_FILE_WITH_MULTIPLE_VIOLATIONS);
        assertThat(violation1.getLine()).isEqualTo(3);
        assertThat(violation1.getMessage()).isEqualTo("[Code:2154] Message: variable is referenced but not assigned.");
        assertThat(violation1.getSeverity()).isEqualTo(Severity.WARNING);

        Violation violation2 = result.getViolations().get(1);
        assertThat(violation2.getFilenameOrJavaClassName()).contains(REVIEW_FILE_WITH_MULTIPLE_VIOLATIONS);
        assertThat(violation2.getLine()).isEqualTo(5);
        assertThat(violation2.getMessage()).isEqualTo("[Code:1037] Message: Braces are required for positionals over 9, e.g. ${10}.");
        assertThat(violation2.getSeverity()).isEqualTo(Severity.ERROR);

        Violation violation3 = result.getViolations().get(2);
        assertThat(violation3.getFilenameOrJavaClassName()).contains(REVIEW_FILE_WITH_MULTIPLE_VIOLATIONS);
        assertThat(violation3.getLine()).isEqualTo(7);
        assertThat(violation3.getMessage()).isEqualTo("[Code:2078] Message: This expression is constant. Did you forget a $ somewhere?");
        assertThat(violation3.getSeverity()).isEqualTo(Severity.ERROR);
    }

    @Test
    public void shouldReturnNoViolationWhenRuleExcludedInConfig() {
        Assumptions.assumeTrue(isShellcheckInstalled());
        Review review = getReview(REVIEW_FILE_WITH_EXCLUDED_VIOLATION);

        ReviewResult result = sut.process(review);

        assertThat(result).isNotNull();
        assertThat(result.getViolations()).isEmpty();
    }

    private boolean isShellcheckInstalled() {
        try {
            String result = new ExternalProcess().executeCommand("shellcheck");
            return StringUtils.isBlank(result);
        } catch (Exception e) {
            return false;
        }
    }

    private Review getReview(String... filePaths) {
        List<ReviewFile> files = new ArrayList<>();
        for (String filePath : filePaths) {
            files.add(new ReviewFile(filePath));
        }
        return new Review(files, ReviewFormatterFactory.get(config));
    }
}
