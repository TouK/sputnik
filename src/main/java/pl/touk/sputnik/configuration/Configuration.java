package pl.touk.sputnik.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Properties;

@Slf4j
public class Configuration {
    private static final String CLI_OPTION_PREFIX = "cli.";
    private final Properties properties;

    Configuration(Properties properties) {
        this.properties = properties;
    }
    
    @Nullable
    @Deprecated
    private String getProperty(@NotNull String key) {
        return getPropertyByKey(key);
    }
    
    @Nullable
    private String getPropertyByKey(@NotNull String key) {
        String value = properties.getProperty(key);
        if (StringUtils.isBlank(value)) {
            value = System.getProperty(key);
        }
        return value;
    }

    @Nullable
    public String getProperty(@NotNull ConfigurationOption confOption) {
        String value = getPropertyByKey(confOption.getKey());
        if (StringUtils.isBlank(value)) {
            value = confOption.getDefaultValue();
        }
        return value;
    }

    public void updateWithCliOptions(CommandLine commandLine) {
        for (Option option : commandLine.getOptions()) {
            properties.setProperty(CLI_OPTION_PREFIX + option.getArgName(), option.getValue());
        }
    }

}
