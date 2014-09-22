package pl.touk.sputnik.processor.codenarc;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pl.touk.sputnik.configuration.ConfigurationHolder;
import pl.touk.sputnik.review.*;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CodeNarcProcessorTest {
    private final CodeNarcProcessor sut = new CodeNarcProcessor();

    private static final String CONFIGURATION_WITH_BASIC_AND_IMPORT_RULE_SET_AND_EXCLUDE = "codeNarc/configuration/configurationWithBasicAndImportRuleSetAndExclude.properties";
    private static final String CONFIGURATION_WITH_BASIC_RULE_SET = "codeNarc/configuration/configurationWithBasicRuleSet.properties";
    private static final String CONFIGURATION_WITH_IMPORT_RULE_SET = "codeNarc/configuration/configurationWithImportRuleSet.properties";
    private static final String CONFIGURATION_WITHOUT_RULE_SET = "codeNarc/configuration/configurationWithoutRuleSet.properties";
    private static final String CONFIGURATION_WITH_BASIC_AND_IMPORT_RULE_SET = "codeNarc/configuration/configurationWithBasicAndImportRuleSet.properties";

    private final String REVIEW_FILE_WITH_ONE_VIOLATION = "src/test/resources/codeNarc/testFiles/FileWithOneViolationLevel2.groovy";
    private final String REVIEW_FILE_WITH_ONE_VIOLATION_PER_EACH_SEVERITY = "src/test/resources/codeNarc/testFiles/FileWithOneViolationPerEachLevel.groovy";
    private final String REVIEW_FILE_WITHOUT_VIOLATIONS = "src/test/resources/codeNarc/testFiles/FileWithoutViolations.groovy";
    private final String REVIEW_FILE_WITH_IMPORT_VIOLATION = "src/test/resources/codeNarc/testFiles/FileWithImportViolation.groovy";

    @Before
    public void setUp() throws Exception {
        ConfigurationHolder.initFromResource(CONFIGURATION_WITH_BASIC_RULE_SET);
    }

    @After
    public void tearDown() throws Exception {
        ConfigurationHolder.reset();
    }

    @Test
    public void shouldReturnSomeViolationsForFile() {
        Review review = getReview(REVIEW_FILE_WITH_ONE_VIOLATION);

        ReviewResult result = sut.process(review);

        assertThat(result).isNotNull();
        assertThat(result.getViolations())
                .hasSize(1)
                .contains(new Violation(REVIEW_FILE_WITH_ONE_VIOLATION, 5, "EmptyTryBlock: The try block is empty", Severity.WARNING));
    }

    @Test
    public void shouldReturnViolationsOfEachLevelForFile() {
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
    public void shouldReturnNoViolationsForPerfectFile() {
        Review review = getReview(REVIEW_FILE_WITHOUT_VIOLATIONS);

        ReviewResult result = sut.process(review);

        assertThat(result).isNotNull();
        assertThat(result.getViolations()).isEmpty();
    }

    @Test
    public void shouldReturnNoViolationsWhenNoFiles() {
        Review review = getReview();

        ReviewResult result = sut.process(review);

        assertThat(result).isNotNull();
        assertThat(result.getViolations()).isEmpty();
    }

    @Test
    public void shouldReturnViolationsFromManyFiles() {
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
    public void shouldReturnViolationsUsingImportRuleSet() {
        ConfigurationHolder.initFromResource(CONFIGURATION_WITH_IMPORT_RULE_SET);
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
    public void shouldReturnViolationsUsingDefaultRuleSetFromResources() {
        ConfigurationHolder.initFromResource(CONFIGURATION_WITHOUT_RULE_SET);
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
    public void shouldReturnViolationsUsingImportAndBasicRuleSets() {
        ConfigurationHolder.initFromResource(CONFIGURATION_WITH_BASIC_AND_IMPORT_RULE_SET);
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
    public void shouldNotReturnViolationsFromExcudedFiles() {
        ConfigurationHolder.initFromResource(CONFIGURATION_WITH_BASIC_AND_IMPORT_RULE_SET_AND_EXCLUDE);
        Review review = getReview(REVIEW_FILE_WITH_ONE_VIOLATION, REVIEW_FILE_WITHOUT_VIOLATIONS, REVIEW_FILE_WITH_ONE_VIOLATION_PER_EACH_SEVERITY);

        ReviewResult result = sut.process(review);

        assertThat(result).isNotNull();
        assertThat(result.getViolations())
                .hasSize(1)
                .containsOnly(
                        new Violation(REVIEW_FILE_WITH_ONE_VIOLATION, 5, "EmptyTryBlock: The try block is empty", Severity.WARNING)
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