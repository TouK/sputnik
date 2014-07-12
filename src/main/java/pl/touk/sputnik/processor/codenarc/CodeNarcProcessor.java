package pl.touk.sputnik.processor.codenarc;

import lombok.extern.slf4j.Slf4j;
import org.codenarc.CodeNarcRunner;
import org.codenarc.analyzer.FilesystemSourceAnalyzer;
import org.codenarc.analyzer.SourceAnalyzer;
import org.codenarc.results.FileResults;
import org.codenarc.results.Results;
import org.codenarc.rule.Rule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.touk.sputnik.review.*;

import java.util.Stack;

@Slf4j
public class CodeNarcProcessor implements ReviewProcessor {

    private static final String PROCESSOR_NAME = "CodeNarc";

    @Nullable
    @Override
    public ReviewResult process(@NotNull Review review) {
        if(noFilesToReview(review)){
            return new ReviewResult();
        }
        CodeNarcRunner codeNarcRunner = prepareCodeNarcRunner(review);
        Results results = codeNarcRunner.execute();
        return parseResults(results);
    }

    private boolean noFilesToReview(Review review) {
        return review.getFiles().isEmpty();
    }

    private ReviewResult parseResults(Results results) {
        ReviewResult reviewResult = new ReviewResult();
        Stack<Results> resultsStack = new Stack<>();
        resultsStack.add(results);
        while (!resultsStack.isEmpty()) {
            Results current = resultsStack.pop();
            if (current.isFile()) {
                FileResults fileResults = (FileResults) current;
                for (Object object : fileResults.getViolations()) {
                    org.codenarc.rule.Violation codeNarcViolation = (org.codenarc.rule.Violation) object;
                    Rule rule = codeNarcViolation.getRule();
                    Violation violation = new Violation(fileResults.getPath(), codeNarcViolation.getLineNumber(), rule.getName() + ": " + codeNarcViolation.getMessage(), getRuleSeverity(rule));
                    reviewResult.add(violation);
                }
            } else {
                for (Object childrenResults : current.getChildren()) {
                    resultsStack.push((Results) childrenResults);
                }
            }
        }
        return reviewResult;
    }

    private Severity getRuleSeverity(Rule rule) {
        switch (rule.getPriority()) {
            case 1:
                return Severity.ERROR;
            case 2:
                return Severity.WARNING;
            case 3:
                return Severity.INFO;
            default:
                throw new RuntimeException("Invalid priority of rule " + rule.getName());
        }
    }

    private CodeNarcRunner prepareCodeNarcRunner(Review review) {
        CodeNarcRunner codeNarcRunner = new CodeNarcRunner();
        codeNarcRunner.setRuleSetFiles("codeNarcRuleSets/basic.xml");
        codeNarcRunner.setSourceAnalyzer(createSourceAnalyzer(review));
        return codeNarcRunner;
    }

    private SourceAnalyzer createSourceAnalyzer(Review review) {
        FilesystemSourceAnalyzer sourceAnalyzer = new FilesystemSourceAnalyzer();
        sourceAnalyzer.setBaseDirectory(".");
        StringBuilder stringBuilder = new StringBuilder();
        for(String filesPath : review.getIOFilenames()){
            stringBuilder.append("**/").append(filesPath).append(",");
        }
        sourceAnalyzer.setIncludes(stringBuilder.toString());
        return sourceAnalyzer;
    }

    @NotNull
    @Override
    public String getName() {
        return PROCESSOR_NAME;
    }
}
