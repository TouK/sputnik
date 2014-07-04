package pl.touk.sputnik.engine;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import pl.touk.sputnik.configuration.ConfigurationSetup;
import pl.touk.sputnik.configuration.GeneralOption;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class VisitorBuilderTest {

    @Test
    public void shouldNotBuildBeforeVisitors() {
        new ConfigurationSetup().setUp(Collections.<String, String>emptyMap());

        assertThat(new VisitorBuilder().buildBeforeReviewVisitors()).isEmpty();
    }

    @Test
    public void shouldNotBuildDisabledBeforeVisitors() {
        new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.PROCESS_TEST_FILES.getKey(), "true"
        ));

        assertThat(new VisitorBuilder().buildBeforeReviewVisitors()).isEmpty();
    }

    @Test
    public void shouldBuildBeforeVisitors() {
        new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.PROCESS_TEST_FILES.getKey(), "false"
        ));

        assertThat(new VisitorBuilder().buildBeforeReviewVisitors()).hasSize(1);
    }

    @Test
    public void shouldNotBuildAfterVisitors() {
        new ConfigurationSetup().setUp(Collections.<String, String>emptyMap());

        assertThat(new VisitorBuilder().buildAfterReviewVisitors()).hasSize(2);
    }

    @Test
    public void shouldNotBuildDisabledAfterVisitors() {
        new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.MAX_NUMBER_OF_COMMENTS.getKey(), "0"
        ));

        assertThat(new VisitorBuilder().buildAfterReviewVisitors()).hasSize(2);
    }

    @Test
    public void shouldBuildAfterVisitors() {
        new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.MAX_NUMBER_OF_COMMENTS.getKey(), "50"
        ));

        assertThat(new VisitorBuilder().buildAfterReviewVisitors()).hasSize(3);
    }

}