package pl.touk.sputnik.connector;

import lombok.Getter;
import pl.touk.sputnik.configuration.ConfigurationHolder;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.engine.VisitorBuilder.ScoreStrategies;

import static org.apache.commons.lang3.Validate.notBlank;

@Getter
public class ConnectorDetails {

    private String host;
    private Integer port;
    private String path;
    private String username;
    private String password;
    private String useHttps;
    private boolean isHttps;

    public ConnectorDetails() {
        buildFromConfiguration();
        validate();
    }

    private void buildFromConfiguration() {
        host = ConfigurationHolder.instance().getProperty(GeneralOption.HOST);
        port = Integer.valueOf(ConfigurationHolder.instance().getProperty(GeneralOption.PORT));
        path = ConfigurationHolder.instance().getProperty(GeneralOption.PATH);
        username = ConfigurationHolder.instance().getProperty(GeneralOption.USERNAME);
        password = ConfigurationHolder.instance().getProperty(GeneralOption.PASSWORD);
        useHttps = ConfigurationHolder.instance().getProperty(GeneralOption.USE_HTTPS);
        isHttps = Boolean.parseBoolean(useHttps);
    }

    private void validate() {
        notBlank(host, "You must provide non blank connector host");
        notBlank(username, "You must provide non blank connector username");
        notBlank(password, "You must provide non blank connector password");

        String strategy = ConfigurationHolder.instance().getProperty(GeneralOption.SCORE_STRATEGY);
        // IllegalArgumentException is thrown when strategy could not be mapped to enum
        ScoreStrategies.valueOf(strategy);
    }

}
