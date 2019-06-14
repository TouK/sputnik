package pl.touk.sputnik.processor.codenarc;

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

class CodeNarcProcessorTest {
    private CodeNarcProcessor sut;

    private static final String CONFIGURATION_WITH_BASIC_AND_IMPORT_RULE_SET_AND_EXCLUDE = "codeNarc/configuration/configurationWithBasicAndImportRuleSetAndExclude.properties";
    private static final String CONFIGURATION_WITH_BASIC_RULE_SET = "codeNarc/configuration/configurationWithBasicRuleSet.properties";
    private static final String CONFIGURATION_WITH_IMPORT_RULE_SET = "codeNarc/configuration/configurationWithImportRuleSet.properties";
    private static final String CONFIGURATION_WITHOUT_RULE_SET = "codeNarc/configuration/configurationWithoutRuleSet.properties";
    private static final String CONFIGURATION_WITH_BASIC_AND_IMPORT_RULE_SET = "codeNarc/configuration/configurationWithBasicAndImportRuleSet.properties";

    private final String REVIEW_FILE_WITH_ONE_VIOLATION = "src/test/resources/codeNarc/testFiles/FileWithOneViolationLevel2.groovy";
    private final String REVIEW_FILE_WITH_ONE_VIOLATION_PER_EACH_SEVERITY = "src/test/resources/codeNarc/testFiles/FileWithOneViolationPerEachLevel.groovy";
    private final String REVIEW_FILE_WITHOUT_VIOLATIONS = "src/test/resources/codeNarc/testFiles/FileWithoutViolations.groovy";
    private final String REVIEW_FILE_WITH_IMPORT_VIOLATION = "src/test/resources/codeNarc/testFiles/FileWithImportViolation.groovy";
    private final String REVIEW_FILE_WITH_NOT_GROOVY_EXTENSION = "src/test/resources/wrongExtension.java";

    private Configuration config;

    @BeforeEach
    void setUp() throws Exception {
        config = ConfigurationBuilder.initFromResource(CONFIGURATION_WITH_BASIC_RULE_SET);
        sut = new CodeNarcProcessor(config);
    }

    @Test
    void shouldReturnSomeViolationsForFile() {
        Review review = getReview(REVIEW_FILE_WITH_ONE_VIOLATION);

        ReviewResult result = sut.process(review);

        assertThat(result).isNotNull();
        assertThat(result.getViolations())
                .hasSize(1)
                .contains(new Violation(REVIEW_FILE_WITH_ONE_VIOLATION, 5, "EmptyTryBlock: The try block is empty", Severity.WARNING));
    }

    @Test
    void shouldReturnViolationsOfEachLevelForFile() {
        Review review = getReview(REVIEW_FILE_WITH_ONE_VIOLATION_PER_EACH_SEVERITY);

        ReviewResult result = sut.process(review);

        assertThat(result).isNotNull();
        assertThat(result.getViolations())
                .hasSize(3)
                .containsOnly(
                        new Violation(REVIEW_FILE_WITH_ONE_VIOLATION_PER_EACH_SEVERITY, 5, "ForLoopShouldBeWhileLoop: The for loop can be simplified to a while loop", Severity.ERROR),
                        new Violation(REVIEW_FILE_WITH_ONE_VIOLATION_PER_EACH_SEVERITY, 9, "AssertWithinFinallyBlock: A finally block within class FileWithOneViolationPerEachLevel contains an assert statement, potentially hiding the original exception, if there is one", Severity.WARNING),
                        new Violation(REVIEW_FILE_WITH_ONE_VIOLATION_PER_EACH_SEVERITY, 13, "EmptyMethod: Violation in class FileWithOneViolationPerEachLevel. The method bar is both empty and not marked with @Override", Severity.INFO)
                );
    }

    @Test
    void shouldReturnNoViolationsForPerfectFile() {
        Review review = getReview(REVIEW_FILE_WITHOUT_VIOLATIONS);

        ReviewResult result = sut.process(review);

        assertThat(result).isNotNull();
        assertThat(result.getViolations()).isEmpty();
    }

    @Test
    void shouldReturnNoViolationsWhenNoFiles() {
        Review review = getReview();

        ReviewResult result = sut.process(review);

        assertThat(result).isNotNull();
        assertThat(result.getViolations()).isEmpty();
    }

    @Test
    void shouldReturnViolationsFromManyFiles() {
        Review review = getReview(REVIEW_FILE_WITH_ONE_VIOLATION, REVIEW_FILE_WITHOUT_VIOLATIONS, REVIEW_FILE_WITH_ONE_VIOLATION_PER_EACH_SEVERITY);

        ReviewResult result = sut.process(review);

        assertThat(result).isNotNull();
        assertThat(result.getViolations())
                .hasSize(4)
                .containsOnly(
                        new Violation(REVIEW_FILE_WITH_ONE_VIOLATION, 5, "EmptyTryBlock: The try block is empty", Severity.WARNING),
                        new Violation(REVIEW_FILE_WITH_ONE_VIOLATION_PER_EACH_SEVERITY, 5, "ForLoopShouldBeWhileLoop: The for loop can be simplified to a while loop", Severity.ERROR),
                        new Violation(REVIEW_FILE_WITH_ONE_VIOLATION_PER_EACH_SEVERITY, 9, "AssertWithinFinallyBlock: A finally block within class FileWithOneViolationPerEachLevel contains an assert statement, potentially hiding the original exception, if there is one", Severity.WARNING),
                        new Violation(REVIEW_FILE_WITH_ONE_VIOLATION_PER_EACH_SEVERITY, 13, "EmptyMethod: Violation in class FileWithOneViolationPerEachLevel. The method bar is both empty and not marked with @Override", Severity.INFO)
                );
    }

    @Test
    void shouldReturnViolationsUsingImportRuleSet() {
        config = ConfigurationBuilder.initFromResource(CONFIGURATION_WITH_IMPORT_RULE_SET);
        sut = new CodeNarcProcessor(config);
        Review review = getReview(REVIEW_FILE_WITH_IMPORT_VIOLATION, REVIEW_FILE_WITH_ONE_VIOLATION);

        ReviewResult result = sut.process(review);

        assertThat(result).isNotNull();
        assertThat(result.getViolations())
                .hasSize(2)
                .containsOnly(
                        new Violation(REVIEW_FILE_WITH_IMPORT_VIOLATION, 1, "UnnecessaryGroovyImport", Severity.INFO),
                        new Violation(REVIEW_FILE_WITH_IMPORT_VIOLATION, 1, "UnusedImport: The [java.util.ArrayList] import is never referenced", Severity.INFO)
                );
    }

    @Test
    void shouldReturnViolationsUsingDefaultRuleSetFromResources() {
        config = ConfigurationBuilder.initFromResource(CONFIGURATION_WITHOUT_RULE_SET);
        sut = new CodeNarcProcessor(config);
        Review review = getReview(REVIEW_FILE_WITH_ONE_VIOLATION_PER_EACH_SEVERITY);

        ReviewResult result = sut.process(review);

        assertThat(result).isNotNull();
        assertThat(result.getViolations())
                .hasSize(3)
                .containsOnly(
                        new Violation(REVIEW_FILE_WITH_ONE_VIOLATION_PER_EACH_SEVERITY, 5, "ForLoopShouldBeWhileLoop: The for loop can be simplified to a while loop", Severity.INFO),
                        new Violation(REVIEW_FILE_WITH_ONE_VIOLATION_PER_EACH_SEVERITY, 9, "AssertWithinFinallyBlock: A finally block within class FileWithOneViolationPerEachLevel contains an assert statement, potentially hiding the original exception, if there is one", Severity.WARNING),
                        new Violation(REVIEW_FILE_WITH_ONE_VIOLATION_PER_EACH_SEVERITY, 13, "EmptyMethod: Violation in class FileWithOneViolationPerEachLevel. The method bar is both empty and not marked with @Override", Severity.WARNING)
                );
    }

    @Test
    void shouldReturnViolationsUsingImportAndBasicRuleSets() {
        config = ConfigurationBuilder.initFromResource(CONFIGURATION_WITH_BASIC_AND_IMPORT_RULE_SET);
        sut = new CodeNarcProcessor(config);
        Review review = getReview(REVIEW_FILE_WITH_ONE_VIOLATION, REVIEW_FILE_WITH_IMPORT_VIOLATION);

        ReviewResult result = sut.process(review);

        assertThat(result).isNotNull();
        assertThat(result.getViolations())
                .hasSize(4)
                .containsOnly(
                        new Violation(REVIEW_FILE_WITH_ONE_VIOLATION, 5, "EmptyTryBlock: The try block is empty", Severity.WARNING),
                        new Violation(REVIEW_FILE_WITH_IMPORT_VIOLATION, 2, "EmptyClass: Class 'FileWithoutViolations' is empty (has no methods, fields or properties). Why would you need a class like this?", Severity.WARNING),
                        new Violation(REVIEW_FILE_WITH_IMPORT_VIOLATION, 1, "UnnecessaryGroovyImport", Severity.INFO),
                        new Violation(REVIEW_FILE_WITH_IMPORT_VIOLATION, 1, "UnusedImport: The [java.util.ArrayList] import is never referenced", Severity.INFO)
                );
    }

    @Test
    void shouldNotReturnViolationsFromExcudedFiles() {
        config = ConfigurationBuilder.initFromResource(CONFIGURATION_WITH_BASIC_AND_IMPORT_RULE_SET_AND_EXCLUDE);
        sut = new CodeNarcProcessor(config);
        Review review = getReview(REVIEW_FILE_WITH_ONE_VIOLATION, REVIEW_FILE_WITHOUT_VIOLATIONS, REVIEW_FILE_WITH_ONE_VIOLATION_PER_EACH_SEVERITY);

        ReviewResult result = sut.process(review);

        assertThat(result).isNotNull();
        assertThat(result.getViolations())
                .hasSize(1)
                .containsOnly(
                        new Violation(REVIEW_FILE_WITH_ONE_VIOLATION, 5, "EmptyTryBlock: The try block is empty", Severity.WARNING)
                );
    }

    @Test
    void shouldReturnNoViolationsForFileWithNotGroovyExtension() {
        Review review = getReview(REVIEW_FILE_WITH_NOT_GROOVY_EXTENSION);

        ReviewResult result = sut.process(review);

        assertThat(result).isNotNull();
        assertThat(result.getViolations()).isEmpty();
    }

    @Test
    void shouldReturnNoViolationsForFileWithoutExtension() {
        Review review = getReview(REVIEW_FILE_WITH_NOT_GROOVY_EXTENSION);

        ReviewResult result = sut.process(review);

        assertThat(result).isNotNull();
        assertThat(result.getViolations()).isEmpty();
    }

    private Review getReview(String... filePaths) {
        List<ReviewFile> files = new ArrayList<>();
        for (String filePath : filePaths) {
            files.add(new ReviewFile(filePath));
        }
        return new Review(files, ReviewFormatterFactory.get(config));
    }
}