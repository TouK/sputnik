package pl.touk.sputnik.configuration;

import lombok.Getter;
import org.apache.commons.cli.*;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.Connectors;

public class CliWrapper {

    @Getter
    private final Options options;

    public CliWrapper() {
        options = createOptions();
    }

    @NotNull
    @SuppressWarnings("unchecked")
    private Options createOptions() {
        Options localOptions = new Options();
        localOptions.addOption(buildOption(CliOption.CONF, true, true));
        localOptions.addOption(buildOption(CliOption.CONNECTOR, true, true));

        localOptions.addOption(buildOption(CliOption.CHANGE_ID, true, false));
        localOptions.addOption(buildOption(CliOption.REVISION_ID, true, false));

        localOptions.addOption(buildOption(CliOption.PULL_REQUEST_ID, true, false));

        return localOptions;
    }

    @NotNull
    public CommandLine parse(@NotNull String[] args) throws ParseException {
        CommandLineParser parser = new BasicParser();
        return parser.parse(options, args);
    }

    @NotNull
    @SuppressWarnings("all")
    private Option buildOption(@NotNull CliOption name, boolean hasArgs, boolean isRequired) {
        return OptionBuilder.withArgName(name.getCommandLineParam())
            .withLongOpt(name.getCommandLineParam())
            .hasArg(hasArgs)
            .isRequired(isRequired)
            .withDescription(name.getDescription())
            .create();
    }

    public Connectors contextSensitiveValidation(CommandLine cli) throws ParseException {
        Connectors connector = connector(cli);
        if (connector == Connectors.GERRIT && cli.hasOption(CliOption.CHANGE_ID.getCommandLineParam()) && cli.hasOption(CliOption.REVISION_ID.getCommandLineParam())) {
            return connector;
        } else if (connector == Connectors.STASH && cli.hasOption(CliOption.PULL_REQUEST_ID.getCommandLineParam())) {
            return connector;
        }

        throw new ParseException("CLI arguments out of context");
    }

    public Connectors connector(CommandLine commandLine) {
        return Connectors.valueOf(commandLine.getOptionValue(CliOption.CONNECTOR.getCommandLineParam()).toUpperCase());
    }
}
