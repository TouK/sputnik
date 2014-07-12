package pl.touk.sputnik.engine;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import pl.touk.sputnik.configuration.ConfigurationSetup;
import pl.touk.sputnik.configuration.GeneralOption;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class ProcessorBuilderTest {

    @Test
    public void shouldNotBuildAnyProcessor() {
        new ConfigurationSetup().setUp(Collections.<String, String>emptyMap());

        assertThat(new ProcessorBuilder().buildProcessors()).isEmpty();
    }

    @Test
    public void shouldBuildDisabledProcessors() {
        new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.CHECKSTYLE_ENABLED.getKey(), "false",
                GeneralOption.FINDBUGS_ENABLED.getKey(), "false",
                GeneralOption.PMD_ENABLED.getKey(), "false",
                GeneralOption.SCALASTYLE_ENABLED.getKey(), "false",
                GeneralOption.CODE_NARC_ENABLED.getKey(), "false"
        ));

        assertThat(new ProcessorBuilder().buildProcessors()).isEmpty();
    }

    @Test
    public void shouldBuildAllProcessors() {
        new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.CHECKSTYLE_ENABLED.getKey(), "true",
                GeneralOption.FINDBUGS_ENABLED.getKey(), "true",
                GeneralOption.PMD_ENABLED.getKey(), "true",
                GeneralOption.SCALASTYLE_ENABLED.getKey(), "true",
                GeneralOption.CODE_NARC_ENABLED.getKey(), "true"
        ));

        assertThat(new ProcessorBuilder().buildProcessors()).hasSize(5);
    }
}