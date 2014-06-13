package pl.touk.sputnik.configuration;

import lombok.extern.slf4j.Slf4j;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.google.common.io.Resources.getResource;
import static com.google.common.io.Resources.newInputStreamSupplier;
import static org.apache.commons.lang3.Validate.notBlank;

@Slf4j
public class ConfigurationHolder {

    private static Configuration INSTANCE;

    public static void initFromFile(String configurationFilename) {
        notBlank(configurationFilename, "You need to provide filename with configuration properties");
        log.info("Initializing configuration properties from file {}", configurationFilename);

        try (FileReader resourceReader = new FileReader(configurationFilename)){
            Properties properties = new Properties();
            properties.load(resourceReader);
            initFromProperties(properties);
        } catch (IOException e) {
            log.error("Configuration initialization failed", e);
            throw new RuntimeException("Configuration file " + configurationFilename + " cannot be loaded");
        }
    }

    public static void initFromResource(String configurationResource) {
        notBlank(configurationResource, "You need to provide url with configuration properties");
        log.info("Initializing configuration properties from url {}", configurationResource);

        try (InputStream resourceStream = newInputStreamSupplier(getResource(configurationResource)).getInput()) {
            Properties properties = new Properties();
            properties.load(resourceStream);
            initFromProperties(properties);
        } catch (IOException e) {
            log.error("Configuration initialization failed", e);
            throw new RuntimeException(e);
        }
    }

    public static void initFromProperties(Properties properties) {
        setInstance(new Configuration(properties));
    }

    public static void reset() {
        INSTANCE = null;
    }

    public static Configuration instance() {
        return INSTANCE;
    }

    private static void setInstance(Configuration instance) {
        INSTANCE = instance;
    }

}
