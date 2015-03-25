package pl.touk.sputnik.review;

import pl.touk.sputnik.configuration.ConfigurationHolder;


public class ReviewFormatterFactory {

    /**
     * @return a review formatter instance
     */
    static ReviewFormatter get() {
        return new ReviewFormatter(ConfigurationHolder.instance());
    }
}
