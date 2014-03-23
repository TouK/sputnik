package pl.touk.sputnik;

import lombok.Getter;
import org.apache.commons.cli.*;
import org.jetbrains.annotations.NotNull;

public class CliOptions {
    public static final String CONF = "conf";
    public static final String CHANGE_ID = "changeId";
    public static final String REVISION_ID = "revisionId";
    @Getter
    private final Options options;

    public CliOptions() {
        options = createOptions();
    }

    @NotNull
    @SuppressWarnings("unchecked")
    private Options createOptions() {
        Options options = new Options();
        options.addOption(buildOption(CONF, true, true, "Configuration properties file"));
        options.addOption(buildOption(CHANGE_ID, true, true, "Gerrit change id"));
        options.addOption(buildOption(REVISION_ID, true, true, "Gerrit revision id"));
        return options;
    }

    @NotNull
    public CommandLine parse(@NotNull String[] args) throws ParseException {
        CommandLineParser parser = new BasicParser();
        return parser.parse(options, args);
    }

    @NotNull
    @SuppressWarnings("all")
    private Option buildOption(@NotNull String name, boolean hasArgs, boolean isRequired, @NotNull String description) {
        return OptionBuilder.withArgName(name)
            .withLongOpt(name)
            .hasArg(hasArgs)
            .isRequired(isRequired)
            .withDescription(description)
            .create();
    }
}
