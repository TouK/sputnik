package pl.touk.sputnik.engine.visitor;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;

@Slf4j
@AllArgsConstructor
public class FilterOutTestFilesVisitor implements BeforeReviewVisitor {

	private final String testDir;

    @Override
    public void beforeReview(@NotNull Review review) {
        log.info("Filtering out test files from review");
        review.setFiles(FluentIterable.from(review.getFiles()).filter(new Predicate<ReviewFile>() {
            @Override
            public boolean apply(ReviewFile file) {
                return !file.getReviewFilename().contains(testDir);
            }
        }).toList());
    }
}
