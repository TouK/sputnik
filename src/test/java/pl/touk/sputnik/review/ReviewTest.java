package pl.touk.sputnik.review;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import pl.touk.sputnik.connector.gerrit.json.ReviewInput;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ReviewTest {

    public Review prepare(boolean reviewTestFiles) {
        List<ReviewFile> reviewList = ImmutableList.of(
                new ReviewFile("/src/main/java/file1.java"),
                new ReviewFile("/src/main/java/file2.java"),
                new ReviewFile("/src/test/java/file1.java"),
                new ReviewFile("/src/test/java/file2.java")
                );

        Review review = new Review(reviewList);

        // Create warnings for all files
        int i = 0;
        for (ReviewFile file : reviewList) {
            i++;
            for (int y = 0; y < i; y++) {
                review.addError(file.getReviewFilename(), new Violation(file.getReviewFilename(), 10 + y, "Test error " + y, Severity.ERROR));
            }
        }
        return review;
    }

    @Test
    public void shouldConvertToReviewInput() {
        //given
        Review review = prepare(true);

        //when
        ReviewInput reviewInput = review.toReviewInput(0);

        //then
        assertThat(reviewInput.comments)
                .hasSize(4);
        assertThat(reviewInput.getReviewCount() == 10);
    }

    @Test
    public void shouldNotProcessTestFiles() {
        //given
        Review review = prepare(false);

        //when
        ReviewInput reviewInput = review.toReviewInput(0);

        //then
        assertThat(reviewInput.comments)
                .hasSize(2)
                .containsKeys("/src/main/java/file1.java", "/src/main/java/file2.java");
        assertThat(reviewInput.getReviewCount() == 3);
    }

    @Test
    public void shouldNotProcessMoreFiles() {
        //given
        Review review = prepare(true);

        //when
        ReviewInput reviewInput = review.toReviewInput(3);



        //then
        assertThat(reviewInput.comments)
                .hasSize(2);

        assertThat(reviewInput.getReviewCount() == 3);
    }

}