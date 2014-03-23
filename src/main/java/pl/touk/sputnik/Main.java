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
        try {
            commandLine = cliOptions.parse(args);
        } catch (ParseException e) {
            printUsage(cliOptions);
            System.out.println(e.getMessage());
            System.exit(1);
        }

        Configuration.instance().setConfigurationFilename(commandLine.getOptionValue(CliOptions.CONF));
        Configuration.instance().setGerritChangeId(commandLine.getOptionValue(CliOptions.CHANGE_ID));
        Configuration.instance().setGerritRevisionId(commandLine.getOptionValue(CliOptions.REVISION_ID));
        Configuration.instance().init();
        new Engine().run();
    }

    private static void printUsage(@NotNull CliOptions cliOptions) {
        System.out.println(HEADER);
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.setWidth(WIDTH);
        helpFormatter.printHelp(SPUTNIK, cliOptions.getOptions(), true);
    }
}
