package pl.touk.sputnik;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.CliOption;
import pl.touk.sputnik.configuration.CliWrapper;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationBuilder;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.configuration.GeneralOptionNotSupportedException;
import pl.touk.sputnik.connector.ConnectorFacade;
import pl.touk.sputnik.connector.ConnectorFacadeFactory;
import pl.touk.sputnik.connector.ConnectorType;
import pl.touk.sputnik.engine.Engine;

public final class Main {
    private static final String SPUTNIK = "sputnik";
    private static final String HEADER = "Sputnik - review your Gerrit patchset with Checkstyle, PMD, SpotBugs and other processors!";
    private static final int WIDTH = 120;

    private Main() {}

    public static void main(String[] args) {
        printWelcomeMessage();
        CliWrapper cliWrapper = new CliWrapper();
        CommandLine commandLine = null;
        try {
            commandLine = cliWrapper.parse(args);
        } catch (ParseException e) {
            printUsage(cliWrapper);
            System.out.println(e.getMessage());
            System.exit(1);
        }

        Configuration configuration = ConfigurationBuilder.initFromFile(commandLine.getOptionValue(CliOption.CONF.getCommandLineParam()));
        configuration.updateWithCliOptions(commandLine);

        ConnectorFacade facade = getConnectorFacade(configuration);
        new Engine(facade, facade, configuration).run();
    }

    private static ConnectorFacade getConnectorFacade(Configuration configuration) {
        ConnectorType connectorType = ConnectorType.getValidConnectorType(configuration.getProperty(GeneralOption.CONNECTOR_TYPE));
        ConnectorFacade facade = null;
        try {
            facade = ConnectorFacadeFactory.INSTANCE.build(connectorType, configuration);
            facade.validate(configuration);
        } catch (GeneralOptionNotSupportedException e) {
            System.out.println(e.getMessage());
            System.exit(2);
        }
        return facade;
    }

    private static void printUsage(@NotNull CliWrapper cliOptions) {
        System.out.println(HEADER);
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.setWidth(WIDTH);
        helpFormatter.printHelp(SPUTNIK, cliOptions.getOptions(), true);
    }

    private static void printWelcomeMessage() {
        System.out.println("Sputnik version " + Main.class.getPackage().getImplementationVersion());
    }
}
