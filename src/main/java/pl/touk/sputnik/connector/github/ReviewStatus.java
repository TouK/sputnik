package pl.touk.sputnik.connector.github;

import lombok.extern.slf4j.Slf4j;
import pl.touk.sputnik.review.Review;

import java.io.IOException;

@Slf4j
class ReviewStatus {

    private static final String OK_STATUS = "All's well. Great job!";
    private static final String NOK_STATUS = "There are some issues with your code. See the linked issue.";

    private Review review;

    public ReviewStatus(Review review) {
        this.review = review;
    }

    public String description(ContentRenderer renderer) {
        if (review.getFiles().isEmpty()) {
            return OK_STATUS;
        } else {
            return renderNotOkMessage(renderer, review);
        }
    }

    private String renderNotOkMessage(ContentRenderer renderer, Review review) {
        try {
            return renderer.render(review);
        } catch (IOException e) {
            log.error("Unable to render message template", e);
            return NOK_STATUS;
        }
    }

    public boolean isAlarming() {
        return !review.getFiles().isEmpty();
    }
}
