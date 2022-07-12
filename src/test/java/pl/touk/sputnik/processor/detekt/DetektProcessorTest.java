package pl.touk.sputnik.processor.detekt;

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

class DetektProcessorTest {
    private static final String CONFIGURATION_WITH_KTLINT_ENABLED_AND_WITH_DETEKT_CONFIG_FILE = "detekt/configuration/configurationWithEnabledDetektAndDetektConfigFile.properties";
    private static final String CONFIGURATION_WITH_KTLINT_ENABLED_AND_WITHOUT_DETEKT_CONFIG_FILE = "detekt/configuration/configurationWithEnabledDetektAndWithoutDetektConfigFile.properties";

    private static final String VIOLATIONS_1 = "src/test/resources/detekt/testFiles/Violations1.kt";
    private static final String VIOLATIONS_2 = "src/test/resources/detekt/testFiles/sub/Violations2.kt";
    private static final String VIOLATIONS_3 = "src/test/resources/detekt/testFiles/Violations3.kt";
    private static final String VIOLATIONS_4 = "src/test/resources/detekt/testFiles/Violations4.kt";
    private static final String REVIEW_GROOVY_FILE = "src/test/resources/codeNarc/testFiles/FileWithOneViolationLevel2.groovy";

    private DetektProcessor sut;
    private Configuration config;

    @BeforeEach
    void setUp() {
        config = ConfigurationBuilder.initFromResource(CONFIGURATION_WITH_KTLINT_ENABLED_AND_WITH_DETEKT_CONFIG_FILE);
        sut = new DetektProcessor(config);
    }

    @Test
    void shouldReturnViolationsOnlyForOneRequestedFile() {
        Review review = getReview(VIOLATIONS_1);

        ReviewResult result = sut.process(review);

        assertThat(result).isNotNull();
        assertThat(result.getViolations())
                .hasSize(3)
                .contains(new Violation(VIOLATIONS_1, 14, "[style/NewLineAtEndOfFile] Checks whether files end with a line separator.", Severity.INFO))
                .contains(new Violation(VIOLATIONS_1, 3, "[style/WildcardImport] Wildcard imports should be replaced with imports using fully qualified class names. Wildcard imports can lead to naming conflicts. A library update can introduce naming clashes with your classes which results in compilation errors.", Severity.INFO))
                .contains(new Violation(VIOLATIONS_1, 7, "[style/MagicNumber] Report magic numbers. Magic number is a numeric literal that is not defined as a constant and hence it's unclear what the purpose of this number is. It's better to declare such numbers as constants and give them a proper name. By default, -1, 0, 1, and 2 are not considered to be magic numbers.", Severity.INFO));
    }

    @Test
    void shouldReturnViolationsOnlyForRequestedFiles() {
        Review review = getReview(VIOLATIONS_2, VIOLATIONS_3);

        ReviewResult result = sut.process(review);

        assertThat(result).isNotNull();
        assertThat(result.getViolations())
                .hasSize(3)
                .contains(new Violation(VIOLATIONS_3, 3, "[empty-blocks/EmptyClassBlock] Empty block of code detected. As they serve no purpose they should be removed.", Severity.INFO))
                .contains(new Violation(VIOLATIONS_2, 3, "[style/NewLineAtEndOfFile] Checks whether files end with a line separator.", Severity.INFO))
                .contains(new Violation(VIOLATIONS_3, 4, "[style/NewLineAtEndOfFile] Checks whether files end with a line separator.", Severity.INFO));
    }

    @Test
    void shouldReturnGlobalScopeViolation() {
        Review review = getReview(VIOLATIONS_4);

        ReviewResult result = sut.process(review);

        assertThat(result).isNotNull();
        assertThat(result.getViolations())
                .hasSize(1)
                .contains(new Violation(VIOLATIONS_4, 7, "[coroutines/GlobalCoroutineUsage] The usage of the `GlobalScope` instance is highly discouraged.", Severity.ERROR));
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
    void shouldProcessReviewsOnDefaultConfig() {
        Configuration configWithoutDetektConfigFile =
                ConfigurationBuilder.initFromResource(CONFIGURATION_WITH_KTLINT_ENABLED_AND_WITHOUT_DETEKT_CONFIG_FILE);
        Review review = getReview(VIOLATIONS_1, VIOLATIONS_2, VIOLATIONS_3, REVIEW_GROOVY_FILE);

        DetektProcessor detektProcessor = new DetektProcessor(configWithoutDetektConfigFile);
        ReviewResult result = detektProcessor.process(review);

        assertThat(result).isNotNull();
    }

    private Review getReview(String... filePaths) {
        List<ReviewFile> files = new ArrayList<>();
        for (String filePath : filePaths) {
            files.add(new ReviewFile(filePath));
        }
        return new Review(files, ReviewFormatterFactory.get(config));
    }
}
