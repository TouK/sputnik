package pl.touk.sputnik.processor.jslint;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewException;
import pl.touk.sputnik.review.ReviewProcessor;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.Severity;
import pl.touk.sputnik.review.Violation;
import pl.touk.sputnik.review.filter.JavaScriptFilter;
import pl.touk.sputnik.review.transformer.IOFileTransformer;

import com.googlecode.jslint4java.Issue;
import com.googlecode.jslint4java.JSLint;
import com.googlecode.jslint4java.JSLintBuilder;
import com.googlecode.jslint4java.JSLintResult;

public class JSLintProcessor implements ReviewProcessor {

    private static final String SOURCE_NAME = "JSLint";

    @Override
    public ReviewResult process(Review review) {
        return toReviewResult(lint(review));
    }
    
    private ReviewResult toReviewResult(List<Issue> lintIssues) {
        ReviewResult result = new ReviewResult();
        for (Issue issue : lintIssues) {
            result.add(new Violation(issue.getSystemId(), issue.getLine(), issue.getReason(), Severity.INFO));
        }
        return result;
    }

    private List<Issue> lint(Review review) {
        JSLint jsLint = new JSLintBuilder().fromDefault();
        List<Issue> lintIssues = new ArrayList<>();
        List<File> files = review.getFiles(new JavaScriptFilter(), new IOFileTransformer());
        for (File file : files) {
            lintIssues.addAll(lintFile(jsLint, file));
        }
        return lintIssues;
    }

    private List<Issue> lintFile(JSLint jsLint, File file) {
        try {
            FileReader fileReader = new FileReader(file);
            JSLintResult lintResult = jsLint.lint(file.getAbsolutePath(), fileReader);
            return lintResult.getIssues();
        } catch (IOException e) {
            throw new ReviewException("IO exception when running JsLint.", e);
        }
    }

    @Override
    public String getName() {
        return SOURCE_NAME;
    }
}
