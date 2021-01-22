package pl.touk.sputnik.connector.gerrit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pl.touk.sputnik.configuration.CliOption;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationOption;
import pl.touk.sputnik.configuration.GeneralOption;

@ExtendWith(MockitoExtension.class)
class GerritFacadeBuilderTest {
    private static final String CHANGE_ID_WITH_SLASH = "project/subproject~branch/subbranch~changeId";
    private static final String REVISION_ID = "changeId";

    @Mock
    private Configuration configuration;

    private GerritFacadeBuilder gerritFacadeBuilder;

    @BeforeEach
    void setup() {
        when(configuration.getProperty(any()))
                .then(invocation -> ((ConfigurationOption)invocation.getArgument(0)).getDefaultValue());
        configure(CliOption.CHANGE_ID, CHANGE_ID_WITH_SLASH);
        configure(CliOption.REVISION_ID, REVISION_ID);

        gerritFacadeBuilder = new GerritFacadeBuilder();
    }

    @Test
    void shouldEscapeChangeIdWithSlash() {
        GerritFacade connector = gerritFacadeBuilder.build(configuration);

        assertThat(connector.gerritPatchset.getChangeId())
                .isEqualTo("project%2Fsubproject~branch%2Fsubbranch~changeId");
        assertThat(connector.gerritPatchset.getRevisionId())
                .isEqualTo(REVISION_ID);
    }

    @Test
    void shouldBuildWithCorrectOptions() {
        configure(GeneralOption.GERRIT_USE_HTTP_PASSWORD, "true");
        configure(GeneralOption.GERRIT_OMIT_DUPLICATE_COMMENTS, "false");

        GerritFacade connector = gerritFacadeBuilder.build(configuration);

        assertThat(connector.options.isUseHttpPassword()).isTrue();
        assertThat(connector.options.isOmitDuplicateComments()).isFalse();
    }

    private void configure(ConfigurationOption option, String value) {
        when(configuration.getProperty(option)).thenReturn(value);
    }
}
