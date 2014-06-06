package pl.touk.sputnik;

import org.apache.commons.cli.CommandLine;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.gerrit.GerritFacade;
import pl.touk.sputnik.stash.StashFacade;

public class ConnectorFacadeFactory {
    public static final ConnectorFacadeFactory INSTANCE = new ConnectorFacadeFactory();

    @NotNull
    public ConnectorFacade get(Connectors name, CommandLine commandLine) {
        if (name == Connectors.GERRIT) {
            return GerritFacade.build(commandLine);
        } else if (name == Connectors.STASH) {
            return StashFacade.build(commandLine);
        }
        throw new RuntimeException("No connector found for " + name);
    }

}
