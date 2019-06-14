package pl.touk.sputnik.configuration;

import lombok.Getter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jetbrains.annotations.NotNull;

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

        localOptions.addOption(buildOption(CliOption.CHANGE_ID, true, false));
        localOptions.addOption(buildOption(CliOption.REVISION_ID, true, false));

        localOptions.addOption(buildOption(CliOption.PULL_REQUEST_ID, true, false));
        localOptions.addOption(buildOption(CliOption.API_KEY, true, false));
        localOptions.addOption(buildOption(CliOption.BUILD_ID, true, false));
        localOptions.addOption(buildOption(CliOption.PROVIDER, true, false));
        localOptions.addOption(buildOption(CliOption.FILE_REGEX, true, false));
        localOptions.addOption(buildOption(CliOption.USERNAME, true, false));
        localOptions.addOption(buildOption(CliOption.PASSWORD, true, false));

        return localOptions;
    }

    @NotNull
    public CommandLine parse(@NotNull String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }

    @NotNull
    @SuppressWarnings("all")
    private Option buildOption(@NotNull CliOption name, boolean hasArgs, boolean isRequired) {
        return Option.builder().argName(name.getCommandLineParam())
            .longOpt(name.getCommandLineParam())
            .hasArg(hasArgs)
            .required(isRequired)
            .desc(name.getDescription())
            .build();
    }
}
