package pl.touk.sputnik.processor.codenarc;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codenarc.results.FileResults;
import org.codenarc.results.Results;
import org.codenarc.rule.Rule;
import pl.touk.sputnik.review.ReviewException;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.Severity;
import pl.touk.sputnik.review.Violation;

import java.util.Stack;

@Slf4j
class ResultParser {
    public ReviewResult parseResults(Results results) {
        ReviewResult reviewResult = new ReviewResult();
        Stack<Results> resultsStack = new Stack<>();
        resultsStack.add(results);
        while (!resultsStack.isEmpty()) {
            parseResult(reviewResult, resultsStack);
        }
        return reviewResult;
    }

    private void parseResult(ReviewResult reviewResult, Stack<Results> resultsStack) {
        Results current = resultsStack.pop();
        if (current.isFile()) {
            parseFileResult(reviewResult, (FileResults) current);
        } else {
            parseDirectoryResult(resultsStack, current);
        }
    }

    private void parseDirectoryResult(Stack<Results> resultsStack, Results current) {
        for (Object child : current.getChildren()) {
            Results childResults = (Results) child;
            if (!childResults.getViolations().isEmpty()) {
                resultsStack.push(childResults);
            }
        }
    }

    private void parseFileResult(ReviewResult reviewResult, FileResults fileResults) {
        for (Object object : fileResults.getViolations()) {
            org.codenarc.rule.Violation codeNarcViolation = (org.codenarc.rule.Violation) object;
            log.debug("Found violation: {}", codeNarcViolation);
            Rule rule = codeNarcViolation.getRule();
            reviewResult.add(createViolation(fileResults, codeNarcViolation, rule));
        }
    }

    private Violation createViolation(FileResults fileResults, org.codenarc.rule.Violation codeNarcViolation, Rule rule) {
        return new Violation(fileResults.getPath(), setLineNumber(codeNarcViolation.getLineNumber()), createViolationMessage(codeNarcViolation, rule), getRuleSeverity(rule));
    }

    private int setLineNumber(Integer lineNumber) {
        return lineNumber == null ? 0 : lineNumber;
    }

    private String createViolationMessage(org.codenarc.rule.Violation codeNarcViolation, Rule rule) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(rule.getName());
        if (StringUtils.isNotBlank(codeNarcViolation.getMessage())) {
            stringBuilder.append(": ").append(codeNarcViolation.getMessage());
        }
        return stringBuilder.toString();
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
                throw new ReviewException("Invalid priority of rule " + rule.getName());
        }
    }
}
