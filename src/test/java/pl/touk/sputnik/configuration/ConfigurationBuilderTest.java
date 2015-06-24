package pl.touk.sputnik.configuration;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConfigurationBuilderTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenConfigFilenameIsEmpty() {
        ConfigurationBuilder.initFromFile("");
    }

    @Test(expected = RuntimeException.class)
    public void shouldFailWhenConfigFileDoesNotExist() {
        ConfigurationBuilder.initFromFile("wrong.properties");
    }

    @Test
    public void shouldReadPropertiesFromFile() {
        Configuration config = ConfigurationBuilder.initFromResource("sample-test.properties");

        assertThat(config.getProperty(GeneralOption.PORT)).isEqualTo("9999");
    }

    @Test
    public void shouldOverrideSystemProperties() {
        System.setProperty(GeneralOption.USERNAME.getKey(), "userala");
        Configuration config = ConfigurationBuilder.initFromResource("sample-test.properties");

        assertThat(config.getProperty(GeneralOption.USERNAME)).isEqualTo("userala");
    }

    @Test
    public void shouldReturnNotOverridedSystemProperties() {
        System.setProperty("some.system.property", "1234");
        Configuration config = ConfigurationBuilder.initFromResource("sample-test.properties");

        assertThat(config.getProperty(GeneralOption.PORT)).isEqualTo("9999");
    }

    @Test
    public void shouldUpdateWithCliOptions() {
        Configuration config = ConfigurationBuilder.initFromResource("sample-test.properties");
        CommandLine commandLineMock = buildCommandLine();

        config.updateWithCliOptions(commandLineMock);

        assertThat(config.getProperty(CliOption.CHANGE_ID)).isEqualTo("99999");
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