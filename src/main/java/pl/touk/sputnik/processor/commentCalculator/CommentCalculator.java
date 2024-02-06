package pl.touk.sputnik.processor.commentCalculator;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewProcessor;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.transformer.FileNameTransformer;
import pl.touk.sputnik.review.Violation;
import pl.touk.sputnik.review.Severity;
import pl.touk.sputnik.review.filter.GroovyFilter;

import java.util.List;

@Slf4j
@AllArgsConstructor
public class CommentCalculator implements ReviewProcessor {
    private static final String PROCESSOR_NAME = "CommentCalculator";

    @Nullable
    @Override
    public ReviewResult process(@NotNull Review review) {
        List<String> reviewFiles = review.getFiles(new GroovyFilter(), new FileNameTransformer());
        if (noFilesToReview(reviewFiles)) {
            return new ReviewResult();
        }
        ReviewResult results = new ReviewResult();
        for(String file : reviewFiles){
            int commentAmount = calculateCommentAmount(file, "//");
            results.add(new Violation(file, 0, "Comment amount: " + commentAmount, Severity.INFO));
        }
        return results;
    }

    private boolean noFilesToReview(List<String> reviewFiles) {
        return reviewFiles.isEmpty();
    }

    @NotNull
    @Override
    public String getName() {
        return PROCESSOR_NAME;
    }

    @NotNull
    private int calculateCommentAmount(String code, String commentPattern) {
        int commentCount = 0;
        int index = 0;
        while (index < code.length()) {
            int commentStartIndex = code.indexOf(commentPattern, index);
            if (commentStartIndex == -1) {
                break;
            }
            commentCount++;
            index = commentStartIndex + commentPattern.length();
        }
        return commentCount;
    }
}
