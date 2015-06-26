package pl.touk.sputnik;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import org.junit.After;
import org.junit.Before;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationBuilder;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;
import pl.touk.sputnik.review.ReviewFormatter;

import java.io.File;

import static com.google.common.io.Resources.getResource;

public abstract class TestEnvironment {

    protected Configuration config;

    @Before
    public void setUp() throws Exception {
        config = ConfigurationBuilder.initFromResource("test.properties");
    }

    protected Review review() {
        return review("java/TestFile.java");
    }

    protected Review review(String filename) {
        return new Review(ImmutableList.of(new ReviewFile(Resources.getResource(filename).getFile())), new ReviewFormatter(config));
    }

    protected Review nonexistantReview() {
        return new Review(ImmutableList.of(new ReviewFile("test")), new ReviewFormatter(config));
    }

    protected Review nonexistantReview(String filename){
        return new Review(ImmutableList.of(new ReviewFile(filename)), new ReviewFormatter(config));
    }

    protected File getResourceAsFile(String resourceName) {
        return new File(getResource(resourceName).getFile());
    }

}
