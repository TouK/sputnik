package pl.touk.sputnik.connector;

import lombok.Getter;
import pl.touk.sputnik.configuration.CliOption;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;

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
    private boolean verifySsl;

    public ConnectorDetails(Configuration configuration) {
        buildFromConfiguration(configuration);
        validate();
    }

    private void buildFromConfiguration(Configuration configuration) {
        host = configuration.getProperty(GeneralOption.HOST);
        port = Integer.valueOf(configuration.getProperty(GeneralOption.PORT));
        path = configuration.getProperty(GeneralOption.PATH);
        username = getUsername(configuration);
        password = getPassword(configuration);
        useHttps = configuration.getProperty(GeneralOption.USE_HTTPS);
        isHttps = Boolean.parseBoolean(useHttps);
        verifySsl = Boolean.parseBoolean(configuration.getProperty(GeneralOption.VERIFY_SSL));
    }

    private String getUsername(Configuration configuration) {
        return configuration.getProperty(CliOption.USERNAME) != null ? configuration.getProperty(
                CliOption.USERNAME) : configuration.getProperty(GeneralOption.USERNAME);
    }

    private String getPassword(Configuration configuration) {
        return configuration.getProperty(CliOption.PASSWORD) != null ? configuration.getProperty(
                CliOption.PASSWORD) : configuration.getProperty(GeneralOption.PASSWORD);
    }

    private void validate() {
        notBlank(host, "You must provide non blank connector host");
        notBlank(username, "You must provide non blank connector username");
        notBlank(password, "You must provide non blank connector password");
    }

}
