package pl.touk.sputnik.connector.stash;

import pl.touk.sputnik.Configuration;

import static org.apache.commons.lang3.Validate.notBlank;

public class StashFacadeBuilder {

    public StashFacade build() {
        String host = Configuration.instance().getProperty(StashFacade.STASH_HOST);
        String port = Configuration.instance().getProperty(StashFacade.STASH_PORT);
        String username = Configuration.instance().getProperty(StashFacade.STASH_USERNAME);
        String password = Configuration.instance().getProperty(StashFacade.STASH_PASSWORD);
        String useHttps = Configuration.instance().getProperty(StashFacade.STASH_USE_HTTPS);

        notBlank(host, "You must provide non blank Stash host");
        notBlank(port, "You must provide non blank Stash port");
        notBlank(username, "You must provide non blank Stash username");
        notBlank(password, "You must provide non blank Stash password");

        return new StashFacade(host, Integer.valueOf(port), username, password, Boolean.parseBoolean(useHttps));
    }
}
