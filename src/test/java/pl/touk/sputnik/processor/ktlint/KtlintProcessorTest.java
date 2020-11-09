package pl.touk.sputnik.processor.ktlint;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationBuilder;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;
import pl.touk.sputnik.review.ReviewFormatterFactory;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.Severity;
import pl.touk.sputnik.review.Violation;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class KtlintProcessorTest {
    private static final String CONFIGURATION_WITH_KTLINT_ENABLED = "ktlint/configuration/configurationWithEnabledKtlint.properties";
    private static final String CONFIGURATION_WITH_KTLINT_ENABLED_AND_EXCLUDE = "ktlint/configuration/configurationWithEnabledKtlintAndExclude.properties";

    private static final String REVIEW_FILE_WITH_ONE_VIOLATION = "src/test/resources/ktlint/testFiles/OneViolation.kt";
    private static final String REVIEW_FILE_WITH_NO_VIOLATIONS = "src/test/resources/ktlint/testFiles/NoViolations.kt";
    private static final String REVIEW_FILE_WITH_MANY_VIOLATIONS = "src/test/resources/ktlint/testFiles/ManyViolations.kt";
    private static final String REVIEW_GROOVY_FILE = "src/test/resources/codeNarc/testFiles/FileWithOneViolationLevel2.groovy";

    private KtlintProcessor sut;
    private Configuration config;

    @BeforeEach
    void setUp() throws Exception {
        config = ConfigurationBuilder.initFromResource(CONFIGURATION_WITH_KTLINT_ENABLED);
        sut = new KtlintProcessor(config);
    }

    @Test
    void shouldReturnOneViolationsForFile() {
        Review review = getReview(REVIEW_FILE_WITH_ONE_VIOLATION);

        ReviewResult result = sut.process(review);

        assertThat(result).isNotNull();
        assertThat(result.getViolations())
                .hasSize(1)
                .contains(new Violation(REVIEW_FILE_WITH_ONE_VIOLATION, 3, "[no-empty-class-body] Unnecessary block (\"{}\") in column 20", Severity.WARNING));
    }

    @Test
    void shouldReturnNoViolationsForNotKotlinFiles() {
        Review review = getReview(REVIEW_GROOVY_FILE);

        ReviewResult result = sut.process(review);

        assertThat(result).isNotNull();
        assertThat(result.getViolations()).isEmpty();
    }

    @Test
    void shouldReturnNoViolationsForEmptyReview() {
        Review review = getReview();

        ReviewResult result = sut.process(review);

        assertThat(result).isNotNull();
        assertThat(result.getViolations()).isEmpty();
    }

    @Test
    void shouldReturnViolationsFromManyFiles() {
        Review review = getReview(REVIEW_FILE_WITH_MANY_VIOLATIONS, REVIEW_FILE_WITH_NO_VIOLATIONS, REVIEW_FILE_WITH_ONE_VIOLATION);

        ReviewResult result = sut.process(review);

        assertThat(result).isNotNull();
        assertThat(result.getViolations())
                .hasSize(11)
                .contains(new Violation(REVIEW_FILE_WITH_MANY_VIOLATIONS, 3, "[no-wildcard-imports] Wildcard import in column 1", Severity.WARNING))
                .contains(new Violation(REVIEW_FILE_WITH_MANY_VIOLATIONS, 6, "[indent] Unexpected indentation (2) (should be 4) in column 1", Severity.WARNING))
                .contains(new Violation(REVIEW_FILE_WITH_MANY_VIOLATIONS, 6, "[curly-spacing] Missing spacing before \"{\" in column 21", Severity.WARNING))
                .contains(new Violation(REVIEW_FILE_WITH_MANY_VIOLATIONS, 7, "[indent] Unexpected indentation (6) (should be 8) in column 1", Severity.WARNING))
                .contains(new Violation(REVIEW_FILE_WITH_MANY_VIOLATIONS, 7, "[no-semi] Unnecessary semicolon in column 17", Severity.WARNING))
                .contains(new Violation(REVIEW_FILE_WITH_MANY_VIOLATIONS, 8, "[indent] Unexpected indentation (6) (should be 8) in column 1", Severity.WARNING))
                .contains(new Violation(REVIEW_FILE_WITH_MANY_VIOLATIONS, 8, "[string-template] Redundant curly braces in column 16", Severity.WARNING))
                .contains(new Violation(REVIEW_FILE_WITH_MANY_VIOLATIONS, 8, "[string-template] Redundant \"toString()\" call in string template in column 23", Severity.WARNING))
                .contains(new Violation(REVIEW_FILE_WITH_MANY_VIOLATIONS, 9, "[indent] Unexpected indentation (2) (should be 4) in column 1", Severity.WARNING))
                .contains(new Violation(REVIEW_FILE_WITH_MANY_VIOLATIONS, 11, "[curly-spacing] Missing spacing before \"{\" in column 14", Severity.WARNING))
                .contains(new Violation(REVIEW_FILE_WITH_ONE_VIOLATION, 3, "[no-empty-class-body] Unnecessary block (\"{}\") in column 20", Severity.WARNING));
    }

    @Test
    void shouldReturnOnlyNotExcludedViolations() {
        KtlintProcessor sut = new KtlintProcessor(ConfigurationBuilder.initFromResource(CONFIGURATION_WITH_KTLINT_ENABLED_AND_EXCLUDE));
        Review review = getReview(REVIEW_FILE_WITH_MANY_VIOLATIONS);

        ReviewResult result = sut.process(review);

        assertThat(result).isNotNull();
        assertThat(result.getViolations())
                .hasSize(5)
                .contains(new Violation(REVIEW_FILE_WITH_MANY_VIOLATIONS, 3, "[no-wildcard-imports] Wildcard import in column 1", Severity.WARNING))
                .contains(new Violation(REVIEW_FILE_WITH_MANY_VIOLATIONS, 6, "[curly-spacing] Missing spacing before \"{\" in column 21", Severity.WARNING))
                .contains(new Violation(REVIEW_FILE_WITH_MANY_VIOLATIONS, 8, "[string-template] Redundant curly braces in column 16", Severity.WARNING))
                .contains(new Violation(REVIEW_FILE_WITH_MANY_VIOLATIONS, 8, "[string-template] Redundant \"toString()\" call in string template in column 23", Severity.WARNING))
                .contains(new Violation(REVIEW_FILE_WITH_MANY_VIOLATIONS, 11, "[curly-spacing] Missing spacing before \"{\" in column 14", Severity.WARNING));
    }

    private Review getReview(String... filePaths) {
        List<ReviewFile> files = new ArrayList<>();
        for (String filePath : filePaths) {
            files.add(new ReviewFile(filePath));
        }
        return new Review(files, ReviewFormatterFactory.get(config));
    }
}
