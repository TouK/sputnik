package pl.touk.sputnik.processor.tslint;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import pl.touk.sputnik.configuration.ConfigurationSetup;
import pl.touk.sputnik.configuration.GeneralOption;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class TSLintScriptTest {

    @Test
    void shouldFailWhenConfigFileIsMissing() {
        final String configFile = "tslint.xml.not.json";

        // given
        new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.TSLINT_CONFIGURATION_FILE.getKey(), configFile));
        TSLintScript tsLint = new TSLintScript(null, configFile);

        // when
        Throwable thrown = catchThrowable(tsLint::validateConfiguration);

        // then
        assertThat(thrown).isInstanceOf(TSLintException.class)
                .hasMessageContaining("Could not find tslint configuration file: " + configFile);
    }

    @Test
    void shouldPassWhenConfigFileIsValid() {
        final String configFile = "src/main/resources/tslint.json";

        // given
        new ConfigurationSetup().setUp(ImmutableMap.of(GeneralOption.TSLINT_CONFIGURATION_FILE.getKey(), configFile));
        TSLintScript tsLint = new TSLintScript(null, configFile);

        // when
        Throwable thrown = catchThrowable(tsLint::validateConfiguration);

        // then
        assertThat(thrown).isNull();
    }

}
