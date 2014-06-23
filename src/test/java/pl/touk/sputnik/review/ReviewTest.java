package pl.touk.sputnik.review;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import pl.touk.sputnik.connector.gerrit.json.ReviewFileComment;
import pl.touk.sputnik.connector.gerrit.json.ReviewInput;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;

public class ReviewTest {
    
    List<ReviewFile> reviewList;
    
    @Before
    public void prepare() {
        reviewList = ImmutableList.of(
                new ReviewFile("/src/main/java/file1.java"),
                new ReviewFile("/src/main/java/file2.java"),
                new ReviewFile("/src/test/java/file1.java"),
                new ReviewFile("/src/test/java/file2.java")
                );
    }

    @Test
    public void shouldConvertToReviewInput() {
        //given
        Review review = new Review(reviewList, true);

        //when
        ReviewInput reviewInput = review.toReviewInput();

        //then
        assertThat(reviewInput.comments)
                .hasSize(4);
    }
    
    @Test
    public void shouldNotProcessTestFiles() {
        //given
        Review review = new Review(reviewList, false);

        //when
        ReviewInput reviewInput = review.toReviewInput();

        //then
        assertThat(reviewInput.comments)
                .hasSize(2)
                .containsEntry("/src/main/java/file1.java", Collections.<ReviewFileComment>emptyList());
        
    }

}