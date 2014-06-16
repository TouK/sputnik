package pl.touk.sputnik.configuration;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConfigurationHolderTest {

    private static final String GERRIT_PORT = "gerrit.port";

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenConfigFilenameIsEmpty() {
        ConfigurationHolder.initFromFile("");
    }

    @Test(expected = RuntimeException.class)
    public void shouldFailWhenConfigFileDoesNotExist() {
        ConfigurationHolder.initFromFile("wrong.properties");
    }

    @Test
    public void shouldReadPropertiesFromFile() {
        ConfigurationHolder.initFromResource("sample-test.properties");

        assertThat(ConfigurationHolder.instance().getProperty("gerrit.port")).isEqualTo("9999");
    }

    @Test
    public void shouldOverrideSystemProperties() {
        System.setProperty(GERRIT_PORT, "1234");
        ConfigurationHolder.initFromResource("sample-test.properties");

        assertThat(ConfigurationHolder.instance().getProperty(GERRIT_PORT)).isEqualTo("9999");
    }

    @Test
    public void shouldReturnNotOverridedSystemProperties() {
        System.setProperty("some.system.property", "1234");
        ConfigurationHolder.initFromResource("sample-test.properties");

        assertThat(ConfigurationHolder.instance().getProperty("some.system.property")).isEqualTo("1234");
    }

    @Test
    public void shouldUpdateWithCliOptions() {
        ConfigurationHolder.initFromResource("sample-test.properties");
        CommandLine commandLineMock = buildCommandLine();

        ConfigurationHolder.instance().updateWithCliOptions(commandLineMock);

        assertThat(ConfigurationHolder.instance().getProperty("cli.changeId")).isEqualTo("99999");
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