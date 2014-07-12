package pl.touk.sputnik.processor.codenarc;

import lombok.extern.slf4j.Slf4j;
import org.codenarc.CodeNarcRunner;
import org.codenarc.analyzer.FilesystemSourceAnalyzer;
import org.codenarc.results.FileResults;
import org.codenarc.results.Results;
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
        CodeNarcRunner codeNarcRunner = new CodeNarcRunner();
        codeNarcRunner.setRuleSetFiles("codeNarcRuleSets/basic.xml");
        FilesystemSourceAnalyzer sourceAnalyzer = new FilesystemSourceAnalyzer();
        sourceAnalyzer.setBaseDirectory(".");
        sourceAnalyzer.setIncludes("**/" + review.getIOFilenames().get(0));
        codeNarcRunner.setSourceAnalyzer(sourceAnalyzer);
        Results results = codeNarcRunner.execute();
        ReviewResult reviewResult = new ReviewResult();
        Stack<Results> resultsStack = new Stack<>();
        resultsStack.add(results);
        while (!resultsStack.isEmpty()) {
            Results current = resultsStack.pop();
            if (current.isFile()) {
                FileResults fileResults = (FileResults) current;
                for (Object object : fileResults.getViolations()) {
                    org.codenarc.rule.Violation codeNarcViolation = (org.codenarc.rule.Violation) object;
                    Violation violation = new Violation(fileResults.getPath(), codeNarcViolation.getLineNumber(), codeNarcViolation.getRule().getName() + ": " + codeNarcViolation.getMessage(), codeNarcViolation.getRule().getPriority() == 2 ? Severity.WARNING : Severity.INFO );
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

    @NotNull
    @Override
    public String getName() {
        return PROCESSOR_NAME;
    }
}
