package pl.touk.sputnik;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.review.Engine;

public final class Main {
    private static final String SPUTNIK = "sputnik";
    private static final String HEADER = "Sputnik - review your Gerrit patchset with Checkstyle, PMD and FindBugs";
    private static final int WIDTH = 120;

    private Main() {}

    public static void main(String[] args) {
        CliOptions cliOptions = new CliOptions();
        CommandLine commandLine = null;
        Connectors connector = null;
        try {
            commandLine = cliOptions.parse(args);
            connector = cliOptions.contextSensitiveValidation(commandLine);
        } catch (ParseException e) {
            printUsage(cliOptions);
            System.out.println(e.getMessage());
            System.exit(1);
        }

        initializeConfiguration(connector, commandLine);

        new Engine().run(connector);
    }

    private static void initializeConfiguration(Connectors connector, CommandLine commandLine) {
        Configuration.instance().setConfigurationFilename(commandLine.getOptionValue(CliOptions.CONF));
        switch (connector) {
            case GERRIT:
                Configuration.instance().setGerritChangeId(commandLine.getOptionValue(CliOptions.CHANGE_ID));
                Configuration.instance().setGerritRevisionId(commandLine.getOptionValue(CliOptions.REVISION_ID));
                break;
            case STASH:
                Configuration.instance().setStashPullRequestId(commandLine.getOptionValue(CliOptions.PULL_REQUEST_ID));
                break;
        }

        Configuration.instance().init();
    }

    private static void printUsage(@NotNull CliOptions cliOptions) {
        System.out.println(HEADER);
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.setWidth(WIDTH);
        helpFormatter.printHelp(SPUTNIK, cliOptions.getOptions(), true);
    }
}
