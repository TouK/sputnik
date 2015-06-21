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
        ConfigurationBuilder.initFromResource("sample-test.properties");

        assertThat(ConfigurationBuilder.instance().getProperty(GeneralOption.PORT)).isEqualTo("9999");
    }

    @Test
    public void shouldOverrideSystemProperties() {
        System.setProperty(GeneralOption.USERNAME.getKey(), "userala");
        ConfigurationBuilder.initFromResource("sample-test.properties");

        assertThat(ConfigurationBuilder.instance().getProperty(GeneralOption.USERNAME)).isEqualTo("userala");
    }

    @Test
    public void shouldReturnNotOverridedSystemProperties() {
        System.setProperty("some.system.property", "1234");
        ConfigurationBuilder.initFromResource("sample-test.properties");

        assertThat(ConfigurationBuilder.instance().getProperty(GeneralOption.PORT)).isEqualTo("9999");
    }

    @Test
    public void shouldUpdateWithCliOptions() {
        ConfigurationBuilder.initFromResource("sample-test.properties");
        CommandLine commandLineMock = buildCommandLine();

        ConfigurationBuilder.instance().updateWithCliOptions(commandLineMock);

        assertThat(ConfigurationBuilder.instance().getProperty(CliOption.CHANGE_ID)).isEqualTo("99999");
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