package pl.touk.sputnik;

import org.apache.commons.cli.CommandLine;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.connector.gerrit.GerritFacade;
import pl.touk.sputnik.connector.stash.StashFacade;

public class ConnectorFacadeFactory {
    public static final ConnectorFacadeFactory INSTANCE = new ConnectorFacadeFactory();

    @NotNull
    public ConnectorFacade get(Connectors name, CommandLine commandLine) {
        switch (name) {
            case STASH:
                return StashFacade.build(commandLine);
            case GERRIT:
                return GerritFacade.build(commandLine);
            default:
                throw new RuntimeException("No connector found for " + name);
        }
    }
}
