package pl.touk.sputnik;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import pl.touk.sputnik.review.Engine;

public final class Main {
    private static final String SPUTNIK = "sputnik";

    private Main() {}

    public static void main(String[] args) {
        CliOptions cliOptions = new CliOptions();
        CommandLine commandLine = null;
        try {
            commandLine = cliOptions.parse(args);
        } catch (ParseException e) {
            new HelpFormatter().printHelp(SPUTNIK, cliOptions.getOptions());
            System.exit(1);
        }

        Configuration.instance().setConfigurationFilename(commandLine.getOptionValue(CliOptions.CONF));
        Configuration.instance().setGerritChangeId(commandLine.getOptionValue(CliOptions.CHANGE_ID));
        Configuration.instance().setGerritRevisionId(commandLine.getOptionValue(CliOptions.REVISION_ID));
        Configuration.instance().init();
        new Engine().run();
    }
}
