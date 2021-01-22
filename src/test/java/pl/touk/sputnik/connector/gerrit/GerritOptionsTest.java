package pl.touk.sputnik.connector.gerrit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static pl.touk.sputnik.configuration.GeneralOption.GERRIT_OMIT_DUPLICATE_COMMENTS;
import static pl.touk.sputnik.configuration.GeneralOption.GERRIT_USE_HTTP_PASSWORD;

import org.junit.jupiter.api.Test;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;

class GerritOptionsTest {

    @Test
    void shouldHttpPasswordOptionBeConsidered() {
        assertThat(optionsFrom(GERRIT_USE_HTTP_PASSWORD, "false").isUseHttpPassword())
                .isFalse();
        assertThat(optionsFrom(GERRIT_USE_HTTP_PASSWORD, "true").isUseHttpPassword())
                .isTrue();
    }

    @Test
    void shouldOmitDumplicateCommentsOptionBeConsidered() {
        assertThat(optionsFrom(GERRIT_OMIT_DUPLICATE_COMMENTS, "false").isOmitDuplicateComments())
                .isFalse();
        assertThat(optionsFrom(GERRIT_OMIT_DUPLICATE_COMMENTS, "true").isOmitDuplicateComments())
                .isTrue();
    }

    @Test
    void shouldDisableOptionalFeaturesByDefault() {
        GerritOptions defaultOptions = GerritOptions.from(mock(Configuration.class));

        assertThat(defaultOptions.isOmitDuplicateComments()).isFalse();
        assertThat(defaultOptions.isUseHttpPassword()).isFalse();
    }

    private GerritOptions optionsFrom(GeneralOption option, String value) {
        return GerritOptions.from(configure(option, value));
    }

    private Configuration configure(GeneralOption option, String value) {
        Configuration configuration = mock(Configuration.class);
        when(configuration.getProperty(option)).thenReturn(value);
        return configuration;
    }

}
