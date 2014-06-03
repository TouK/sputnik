package pl.touk.sputnik;

import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.gerrit.GerritFacade;
import pl.touk.sputnik.stash.StashFacade;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.Validate.notBlank;

public class ConnectorFacadeFactory {
    private static final ConnectorFacadeFactory INSTANCE = new ConnectorFacadeFactory();

    private final Map<Connectors, ConnectorFacade> connectors = new HashMap<Connectors, ConnectorFacade>();

    @NotNull
    public static ConnectorFacade get(Connectors name) {
        if (!INSTANCE.connectors.containsKey(name)) {
           INSTANCE.tryToRegister(name);
        }
        return INSTANCE.connectors.get(name);
    }

    private void tryToRegister(Connectors name) {
        if (name == Connectors.GERRIT) {
            register(name, createGerritFacade());
        } else if (name == Connectors.STASH) {
            register(name, createStashFacade());
        }
    }

    private void register(Connectors name, ConnectorFacade facade) {
        connectors.put(name, facade);
    }

    @NotNull
    private GerritFacade createGerritFacade() {
        String host = Configuration.instance().getProperty(GerritFacade.GERRIT_HOST);
        String port = Configuration.instance().getProperty(GerritFacade.GERRIT_PORT);
        String username = Configuration.instance().getProperty(GerritFacade.GERRIT_USERNAME);
        String password = Configuration.instance().getProperty(GerritFacade.GERRIT_PASSWORD);

        notBlank(host, "You must provide non blank Gerrit host");
        notBlank(port, "You must provide non blank Gerrit port");
        notBlank(username, "You must provide non blank Gerrit username");
        notBlank(password, "You must provide non blank Gerrit password");

        return new GerritFacade(host, Integer.valueOf(port), username, password);
    }

    @NotNull
    private static StashFacade createStashFacade() {
        String host = Configuration.instance().getProperty(StashFacade.STASH_HOST);
        String port = Configuration.instance().getProperty(StashFacade.STASH_PORT);
        String username = Configuration.instance().getProperty(StashFacade.STASH_USERNAME);
        String password = Configuration.instance().getProperty(StashFacade.STASH_PASSWORD);

        notBlank(host, "You must provide non blank Stash host");
        notBlank(port, "You must provide non blank Stash port");
        notBlank(username, "You must provide non blank Stash username");
        notBlank(password, "You must provide non blank Stash password");

        return new StashFacade(host, Integer.valueOf(port), username, password);
    }
}
