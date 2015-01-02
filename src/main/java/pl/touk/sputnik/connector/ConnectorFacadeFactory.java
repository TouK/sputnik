package pl.touk.sputnik.connector;

import org.jetbrains.annotations.NotNull;

import pl.touk.sputnik.connector.gerrit.GerritFacadeBuilder;
import pl.touk.sputnik.connector.stash.StashFacadeBuilder;

public class ConnectorFacadeFactory {
    public static final ConnectorFacadeFactory INSTANCE = new ConnectorFacadeFactory();

    GerritFacadeBuilder gerritFacadeBuilder = new GerritFacadeBuilder();
    StashFacadeBuilder stashFacadeBuilder = new StashFacadeBuilder();

    @NotNull
    public ConnectorFacade build(ConnectorType type) {
        switch (type) {
            case STASH:
                return stashFacadeBuilder.build();
            case GERRIT:
                return gerritFacadeBuilder.build();
            default:
                throw new IllegalArgumentException("Connector " + type.getName() + " is not supported");
        }
    }
}
