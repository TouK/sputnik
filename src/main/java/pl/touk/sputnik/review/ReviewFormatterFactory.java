package pl.touk.sputnik.review;

import pl.touk.sputnik.configuration.ConfigurationHolder;


public class ReviewFormatterFactory {

    static ReviewFormatter get() {
        return new ReviewFormatter(ConfigurationHolder.instance());
    }
}
