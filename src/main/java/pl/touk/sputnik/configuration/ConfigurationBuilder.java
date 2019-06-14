package pl.touk.sputnik.configuration;

import com.google.common.base.Charsets;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;
import lombok.extern.slf4j.Slf4j;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import static com.google.common.io.Resources.getResource;
import static org.apache.commons.lang3.Validate.notBlank;

@Slf4j
public class ConfigurationBuilder {

    public static Configuration initFromFile(String configurationFilename) {
        notBlank(configurationFilename, "You need to provide filename with configuration properties");
        log.info("Initializing configuration properties from file {}", configurationFilename);

        try (FileReader resourceReader = new FileReader(configurationFilename)){
            Properties properties = new Properties();
            properties.load(resourceReader);
            return initFromProperties(properties);
        } catch (IOException e) {
            log.error("Configuration initialization failed", e);
            throw new RuntimeException("Configuration file " + configurationFilename + " cannot be loaded");
        }
    }

    public static Configuration initFromResource(String configurationResource) {
        notBlank(configurationResource, "You need to provide url with configuration properties");
        log.info("Initializing configuration properties from url {}", configurationResource);

        CharSource charSource = Resources.asCharSource(getResource(configurationResource), Charsets.UTF_8);
        try (Reader resourceStream = charSource.openStream()) {
            Properties properties = new Properties();
            properties.load(resourceStream);
            return initFromProperties(properties);
        } catch (IOException e) {
            log.error("Configuration initialization failed", e);
            throw new RuntimeException(e);
        }
    }

    public static Configuration initFromProperties(Properties properties) {
        return new Configuration(properties);
    }


}
