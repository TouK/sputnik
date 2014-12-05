package pl.touk.sputnik;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import org.junit.After;
import org.junit.Before;
import pl.touk.sputnik.configuration.ConfigurationHolder;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;

import java.io.File;

import static com.google.common.io.Resources.getResource;

public abstract class TestEnvironment {

    @Before
    public void setUp() throws Exception {
        ConfigurationHolder.initFromResource("test.properties");
    }

    @After
    public void tearDown() throws Exception {
        ConfigurationHolder.reset();
    }

    protected Review review() {
        return review("java/TestFile.java");
    }

    protected Review review(String filename) {
        return new Review(ImmutableList.of(new ReviewFile(Resources.getResource(filename).getFile())));
    }

    protected Review nonexistantReview() {
        return new Review(ImmutableList.of(new ReviewFile("test")));
    }

    protected File getResourceAsFile(String resourceName) {
        return new File(getResource(resourceName).getFile());
    }

}
