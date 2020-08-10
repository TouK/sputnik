package pl.touk.sputnik.connector.gerrit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;

class GerritOptionsTest {

    @Test
    void useHttpPassword() {
        assertFalse(optionsFrom(GeneralOption.GERRIT_USE_HTTP_PASSWORD, "false").isUseHttpPassword());
        assertTrue(optionsFrom(GeneralOption.GERRIT_USE_HTTP_PASSWORD, "true").isUseHttpPassword());
    }

    @Test
    void omitDuplicateComments() {
        assertFalse(optionsFrom(GeneralOption.GERRIT_OMIT_DUPLICATE_COMMENTS, "false").isOmitDuplicateComments());
        assertTrue(optionsFrom(GeneralOption.GERRIT_OMIT_DUPLICATE_COMMENTS, "true").isOmitDuplicateComments());
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
