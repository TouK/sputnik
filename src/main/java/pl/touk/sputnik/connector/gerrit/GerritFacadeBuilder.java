package pl.touk.sputnik.connector.gerrit;

import pl.touk.sputnik.Configuration;

import static org.apache.commons.lang3.Validate.notBlank;

public class GerritFacadeBuilder {

    public GerritFacade build() {
        String host = Configuration.instance().getProperty(GerritFacade.GERRIT_HOST);
        String port = Configuration.instance().getProperty(GerritFacade.GERRIT_PORT);
        String username = Configuration.instance().getProperty(GerritFacade.GERRIT_USERNAME);
        String password = Configuration.instance().getProperty(GerritFacade.GERRIT_PASSWORD);
        String useHttps = Configuration.instance().getProperty(GerritFacade.GERRIT_USE_HTTPS);

        notBlank(host, "You must provide non blank Gerrit host");
        notBlank(port, "You must provide non blank Gerrit port");
        notBlank(username, "You must provide non blank Gerrit username");
        notBlank(password, "You must provide non blank Gerrit password");

        return new GerritFacade(host, Integer.valueOf(port), username, password, Boolean.parseBoolean(useHttps));
    }
}
