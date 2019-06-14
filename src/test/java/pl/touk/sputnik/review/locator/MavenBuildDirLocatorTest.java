package pl.touk.sputnik.review.locator;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationBuilder;
import pl.touk.sputnik.review.Paths;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;
import pl.touk.sputnik.review.ReviewFormatterFactory;

import static org.assertj.core.api.Assertions.assertThat;

class MavenBuildDirLocatorTest {

    private MavenBuildDirLocator locator = new MavenBuildDirLocator(Paths.SRC_MAIN,Paths.SRC_TEST);
    private Configuration config;

    @BeforeEach
    void setUp() {
        config = ConfigurationBuilder.initFromResource("test.properties");
    }

    @Test
    void shouldReturnTestJavaBuildDirectory() {
        Review review = review("gerrit-server/src/test/java/com/google/gerrit/server/project/RefControlTest.java");

        //expect
        assertThat(locator.getBuildDirs(review)).containsExactly("gerrit-server/target/test-classes");
    }

    @Test
    void shouldReturnMainJavaBuildDirectory() {
        Review review = review("gerrit-server/src/main/java/com/google/gerrit/server/project/RefControl.java");

        //expect
        assertThat(locator.getBuildDirs(review)).containsExactly("gerrit-server/target/classes");
    }


    private Review review(String file) {
        return new Review(ImmutableList.of(new ReviewFile(file)), ReviewFormatterFactory.get(config));
    }

}