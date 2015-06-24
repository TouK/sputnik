package pl.touk.sputnik.review;

import pl.touk.sputnik.configuration.Configuration;


public class ReviewFormatterFactory {

    static ReviewFormatter get(Configuration configuration) {
        return new ReviewFormatter(configuration);
    }
}
