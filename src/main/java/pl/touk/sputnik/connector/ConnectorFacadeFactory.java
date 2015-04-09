package pl.touk.sputnik.connector;

import org.jetbrains.annotations.NotNull;

import pl.touk.sputnik.configuration.GeneralOptionNotSupportedException;
import pl.touk.sputnik.connector.gerrit.GerritFacadeBuilder;
import pl.touk.sputnik.connector.github.GithubFacadeBuilder;
import pl.touk.sputnik.connector.stash.StashFacadeBuilder;

public class ConnectorFacadeFactory {
    public static final ConnectorFacadeFactory INSTANCE = new ConnectorFacadeFactory();

    GerritFacadeBuilder gerritFacadeBuilder = new GerritFacadeBuilder();
    StashFacadeBuilder stashFacadeBuilder = new StashFacadeBuilder();
    GithubFacadeBuilder githubFacadeBuilder = new GithubFacadeBuilder();

    @NotNull
    public ConnectorFacade build(@NotNull ConnectorType type) {
        switch (type) {
            case STASH:
                return stashFacadeBuilder.build();
            case GERRIT:
                return gerritFacadeBuilder.build();
            case GITHUB:
                return githubFacadeBuilder.build();
            default:
                throw new GeneralOptionNotSupportedException("Connector " + type.getName() + " is not supported");
        }
    }
}
