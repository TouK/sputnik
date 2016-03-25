package pl.touk.sputnik.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
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
        return Optional.ofNullable(properties.getProperty(key))
                .filter(StringUtils::isNotBlank)
                .orElseGet(() -> System.getProperty(key));
    }

    @Nullable
    public String getProperty(@NotNull ConfigurationOption confOption) {
        return Optional.ofNullable(getPropertyByKey(confOption.getKey()))
                .filter(StringUtils::isNotBlank)
                .orElseGet(confOption::getDefaultValue);
    }

    public void updateWithCliOptions(CommandLine commandLine) {
        for (Option option : commandLine.getOptions()) {
            properties.setProperty(CLI_OPTION_PREFIX + option.getArgName(), option.getValue());
        }
    }

}
