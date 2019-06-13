package pl.touk.sputnik.engine;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationSetup;
import pl.touk.sputnik.configuration.GeneralOption;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class ProcessorBuilderTest {

    @Test
    void shouldNotBuildAnyProcessor() {
        Configuration config = new ConfigurationSetup().setUp(Collections.<String, String>emptyMap());

        assertThat(ProcessorBuilder.buildProcessors(config)).isEmpty();
    }

    @Test
    void shouldNotBuildDisabledProcessors() {
        Configuration config = new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.CHECKSTYLE_ENABLED.getKey(), "false",
                GeneralOption.SPOTBUGS_ENABLED.getKey(), "false",
                GeneralOption.PMD_ENABLED.getKey(), "false",
                GeneralOption.SCALASTYLE_ENABLED.getKey(), "false",
                GeneralOption.CODE_NARC_ENABLED.getKey(), "false"
        ));

        assertThat(ProcessorBuilder.buildProcessors(config)).isEmpty();
    }

    @Test
    void shouldBuildAllProcessors() {
        Configuration config = new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.CHECKSTYLE_ENABLED.getKey(), "true",
                GeneralOption.SPOTBUGS_ENABLED.getKey(), "true",
                GeneralOption.PMD_ENABLED.getKey(), "true",
                GeneralOption.SCALASTYLE_ENABLED.getKey(), "true",
                GeneralOption.CODE_NARC_ENABLED.getKey(), "true"
        ));

        assertThat(ProcessorBuilder.buildProcessors(config)).hasSize(5);
    }

    @Test
    void shouldBuildSpotBugsProcessor() {
        Configuration config = new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.SPOTBUGS_ENABLED.getKey(), "true"
        ));

        assertThat(ProcessorBuilder.buildProcessors(config)).hasSize(1);
    }

    @Test
    void shouldBuildSpotBugsProcessorWithFindBugsProperty() {
        Configuration config = new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.FINDBUGS_ENABLED.getKey(), "true"
        ));

        assertThat(ProcessorBuilder.buildProcessors(config)).hasSize(1);
    }

    @Test
    void shouldBuildSpotBugsIfOnlySpotBugsIsEnabled() {
        Configuration config = new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.SPOTBUGS_ENABLED.getKey(), "true",
                GeneralOption.FINDBUGS_ENABLED.getKey(), "false"
        ));

        assertThat(ProcessorBuilder.buildProcessors(config)).hasSize(1);
    }

    @Test
    void shouldBuildSpotBugsIfOnlyFindBugsIsEnabled() {
        Configuration config = new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.SPOTBUGS_ENABLED.getKey(), "false",
                GeneralOption.FINDBUGS_ENABLED.getKey(), "true"
        ));

        assertThat(ProcessorBuilder.buildProcessors(config)).hasSize(1);
    }
}
