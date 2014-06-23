package pl.touk.sputnik.review;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import pl.touk.sputnik.connector.gerrit.json.ReviewFileComment;
import pl.touk.sputnik.connector.gerrit.json.ReviewInput;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class ReviewTest {

    @Test
    public void shouldConvertToReviewInput() {
        //given
        Review review = new Review(ImmutableList.of(new ReviewFile("test")), true);

        //when
        ReviewInput reviewInput = review.toReviewInput();

        //then
        assertThat(reviewInput.comments)
                .hasSize(1)
                .containsEntry("test", Collections.<ReviewFileComment>emptyList());
    }

}