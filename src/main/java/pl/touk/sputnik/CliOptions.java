package pl.touk.sputnik;

import lombok.Getter;
import org.apache.commons.cli.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

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
        options.addOption(CONF, true, "Configuration properties file");
        options.addOption(CHANGE_ID, true, "Gerrit change id");
        options.addOption(REVISION_ID, true, "Gerrit revision id");
        options.getRequiredOptions().addAll(Arrays.asList(CONF, CHANGE_ID, REVISION_ID));
        return options;
    }

    @NotNull
    public CommandLine parse(@NotNull String[] args) throws ParseException {
        CommandLineParser parser = new BasicParser();
        return parser.parse(options, args);
    }
}
