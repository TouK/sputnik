package pl.touk.sputnik.connector.github;

import pl.touk.sputnik.review.Review;

public class ReviewStatus {

    private static final String OK_STATUS = "All's well. Great job!";
    private static final String NOK_STATUS = "There are some issues with your code";

    private Review review;

    public ReviewStatus(Review review) {
        this.review = review;
    }

    public String description() {
        if (review.getFiles().isEmpty()) {
            return OK_STATUS;
        } else {
            return NOK_STATUS;
        }
    }

    public boolean isAlarming() {
        return !review.getFiles().isEmpty();
    }
}
