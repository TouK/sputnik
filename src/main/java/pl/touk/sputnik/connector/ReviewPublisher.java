package pl.touk.sputnik.connector;

import pl.touk.sputnik.review.Review;

public interface ReviewPublisher {

    void publish(Review review);

}
