package pl.touk.sputnik.processor.tslint;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import pl.touk.sputnik.configuration.ConfigurationSetup;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.review.ReviewException;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;

public class TSLintScriptTest {

    @Test
    public void shouldFailWhenConfigFileIsMissing() {
        final String configFile = "tslint.xml.not.json";

        // given
        new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.TSLINT_CONFIGURATION_FILE.getKey(), configFile));
        TSLintScript tsLint = new TSLintScript(null, configFile);

        // when
        catchException(tsLint).validateConfiguration();

        // then
        assertThat(caughtException()).isInstanceOf(ReviewException.class).hasMessageContaining(
                "Could not find tslint configuration file: " + configFile);
    }

    @Test
    public void shouldPassWhenConfigFileIsValid() {
        final String configFile = "src/main/resources/tslint.json";

        // given
        new ConfigurationSetup().setUp(ImmutableMap.of(GeneralOption.TSLINT_CONFIGURATION_FILE.getKey(), configFile));
        TSLintScript tsLint = new TSLintScript(null, configFile);

        // when
        catchException(tsLint).validateConfiguration();

        // then
        assertThat(caughtException()).isNull();
    }

}
