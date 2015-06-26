package pl.touk.sputnik.review.locator;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationBuilder;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;
import pl.touk.sputnik.review.ReviewFormatterFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class MavenBuildDirLocatorTest {

    private MavenBuildDirLocator locator = new MavenBuildDirLocator();
    private Configuration config;

    @Before
    public void setUp() {
        config = ConfigurationBuilder.initFromResource("test.properties");
    }

    @Test
    public void shouldReturnTestJavaBuildDirectory() {
        //given
        Review review = review("gerrit-server/src/test/java/com/google/gerrit/server/project/RefControlTest.java");

        //expect
        assertThat(locator.getBuildDirs(review)).containsExactly("gerrit-server/target/test-classes");
    }

    @Test
    public void shouldReturnMainJavaBuildDirectory() {
        //given
        Review review = review("gerrit-server/src/main/java/com/google/gerrit/server/project/RefControl.java");

        //expect
        assertThat(locator.getBuildDirs(review)).containsExactly("gerrit-server/target/classes");
    }


    private Review review(String file) {
        return new Review(ImmutableList.of(new ReviewFile(file)), ReviewFormatterFactory.get(config));
    }

}