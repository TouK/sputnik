package pl.touk.sputnik.configuration;

import java.io.FileReader;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Properties;

import static org.apache.commons.lang3.Validate.notBlank;

@Slf4j
public class Configuration {
    private static Configuration INSTANCE = new Configuration();
    private static final String CLI_OPTION_PREFIX = "cli.";
    private Properties properties = new Properties();

    @Getter @Setter private String configurationFilename;

    Configuration() {}

    @NotNull
    public static Configuration instance() {
        return INSTANCE;
    }

    @Nullable
    public String getProperty(@NotNull String key) {
        String value = properties.getProperty(key);
        if (StringUtils.isBlank(value)) {
            value = System.getProperty(key);
        }
        return value;
    }

    @Nullable
    public String getProperty(CliOption cliOption) {
        return getProperty(cliOption.getKey());
    }

    public void init() {
        notBlank(configurationFilename, "You need to provide filename with configuration properties");
        log.info("Initializing configuration properties from file {}", configurationFilename);

        properties = new Properties();
        try (FileReader reader = new FileReader(configurationFilename)){
            properties.load(reader);
        } catch (IOException e) {
            log.error("Configuration initialization failed", e);
            throw new RuntimeException("Configuration file " + configurationFilename + " cannot be loaded");
        }
    }

    public void updateWithCliOptions(CommandLine commandLine) {
        for (Option option : commandLine.getOptions()) {
            properties.setProperty(CLI_OPTION_PREFIX + option.getArgName(), option.getValue());
        }
    }

    static void setInstance(Configuration instance) {
        INSTANCE = instance;
    }

    void setProperties(Properties properties) {
        this.properties = properties;
    }
}
