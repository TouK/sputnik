package pl.touk.sputnik.configuration;

import java.util.Map;
import java.util.Properties;

public class ConfigurationSetup {

    public void setUp(Map<String, String> propertiesMap) {
        Properties properties = new Properties();
        for (Map.Entry<String, String> entry : propertiesMap.entrySet()) {
            properties.setProperty(entry.getKey(), entry.getValue());
        }

        Configuration configuration = new Configuration();
        configuration.setProperties(properties);
        Configuration.setInstance(configuration);
    }
}
