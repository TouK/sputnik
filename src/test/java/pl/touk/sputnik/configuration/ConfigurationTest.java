package pl.touk.sputnik.configuration;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.junit.Before;
import org.junit.Test;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConfigurationTest {

    private static final String GERRIT_PORT = "gerrit.port";

    private Configuration configuration;

    @Before
    public void setUp() {
        configuration = new Configuration();
    }

    @Test
    public void shouldFailWhenConfigFilenameIsEmpty() {
        catchException(configuration).init();

        assertThat(caughtException()).isInstanceOf(NullPointerException.class)
                .hasMessage("You need to provide filename with configuration properties");
    }

    @Test
    public void shouldFailWhenConfigFileDoesNotExist() {
        configuration.setConfigurationFilename("wrong.properties");

        catchException(configuration).init();

        assertThat(caughtException()).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Configuration file wrong.properties cannot be loaded");
    }

    @Test
    public void shouldReadPropertiesFromFile() {
        configuration.setConfigurationFilename("src/test/resources/sample-test.properties");

        configuration.init();

        assertThat(configuration.getProperty("gerrit.port")).isEqualTo("9999");
    }

    @Test
    public void shouldOverrideSystemProperties() {
        System.setProperty(GERRIT_PORT, "1234");
        configuration.setConfigurationFilename("src/test/resources/sample-test.properties");

        configuration.init();

        assertThat(configuration.getProperty(GERRIT_PORT)).isEqualTo("9999");
    }

    @Test
    public void shouldReturnNotOverridedSystemProperties() {
        System.setProperty("some.system.property", "1234");
        configuration.setConfigurationFilename("src/test/resources/sample-test.properties");

        configuration.init();

        assertThat(configuration.getProperty("some.system.property")).isEqualTo("1234");
    }

    @Test
    public void shouldUpdateWithCliOptions() {
        configuration.setConfigurationFilename("src/test/resources/sample-test.properties");
        CommandLine commandLineMock = buildCommandLine();

        configuration.init();
        configuration.updateWithCliOptions(commandLineMock);

        assertThat(configuration.getProperty("cli.changeId")).isEqualTo("99999");
    }

    private CommandLine buildCommandLine() {
        CommandLine commandLineMock = mock(CommandLine.class);
        Option optionMock = mock(Option.class);
        when(optionMock.getArgName()).thenReturn("changeId");
        when(optionMock.getValue()).thenReturn("99999");
        when(commandLineMock.getOptions()).thenReturn(new Option[]{optionMock});
        return commandLineMock;
    }


}