package pl.touk.sputnik.connector;

import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOptionNotSupportedException;
import pl.touk.sputnik.connector.gerrit.GerritFacadeBuilder;
import pl.touk.sputnik.connector.github.GithubFacadeBuilder;
import pl.touk.sputnik.connector.local.LocalFacadeBuilder;
import pl.touk.sputnik.connector.saas.SaasFacadeBuilder;
import pl.touk.sputnik.connector.stash.StashFacadeBuilder;

public class ConnectorFacadeFactory {
    public static final ConnectorFacadeFactory INSTANCE = new ConnectorFacadeFactory();

    GerritFacadeBuilder gerritFacadeBuilder = new GerritFacadeBuilder();
    StashFacadeBuilder stashFacadeBuilder = new StashFacadeBuilder();
    GithubFacadeBuilder githubFacadeBuilder = new GithubFacadeBuilder();
    SaasFacadeBuilder saasFacadeBuilder = new SaasFacadeBuilder();
    LocalFacadeBuilder localFacadeBuilder = new LocalFacadeBuilder();

    @NotNull
    public ConnectorFacade build(@NotNull ConnectorType type, Configuration configuration) {
        switch (type) {
            case STASH:
                return stashFacadeBuilder.build(configuration);
            case GERRIT:
                return gerritFacadeBuilder.build(configuration);
            case GITHUB:
                return githubFacadeBuilder.build(configuration);
            case SAAS:
                return saasFacadeBuilder.build(configuration);
            case LOCAL:
                return localFacadeBuilder.build();
            default:
                throw new GeneralOptionNotSupportedException("Connector " + type.getName() + " is not supported");
        }
    }
}
