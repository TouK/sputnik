package pl.touk.sputnik;

import org.apache.commons.cli.CommandLine;
import org.junit.jupiter.api.Test;
import pl.touk.sputnik.configuration.CliOption;
import pl.touk.sputnik.configuration.CliWrapper;

import static pl.touk.sputnik.SputnikAssertions.assertThat;

class CliOptionsTest {

    private static final String SAMPLE_CONFIG = "/home/spoonman/sputnik/conf.properties";
    private static final String SAMPLE_CHANGE_ID = "I0a2afb7ae4a94ab1ab473ba00e2ec7de381799a0";
    private static final String SAMPLE_REVISION_ID = "3f37692af2290e8e3fd16d2f43701c24346197f0";
    private static final String SAMPLE_PULL_REQUEST_ID = "123";

    private CliWrapper fixture = new CliWrapper();

    @Test
    void shouldExecuteGerritReview() throws Exception {
        String[] args = toArgs("-conf %s -changeId %s -revisionId %s",
                SAMPLE_CONFIG, SAMPLE_CHANGE_ID, SAMPLE_REVISION_ID);

        CommandLine commandLine = fixture.parse(args);

        cliAssert(commandLine).hasOption(CliOption.CONF.getCommandLineParam()).withValue(SAMPLE_CONFIG);
        cliAssert(commandLine).hasOption(CliOption.CHANGE_ID.getCommandLineParam()).withValue(SAMPLE_CHANGE_ID);
        cliAssert(commandLine).hasOption(CliOption.REVISION_ID.getCommandLineParam()).withValue(SAMPLE_REVISION_ID);
    }

    @Test
    void shouldExecuteStashReview() throws Exception {
        String[] args = toArgs("-conf %s -pullRequestId %s", SAMPLE_CONFIG, SAMPLE_PULL_REQUEST_ID);

        CommandLine commandLine = fixture.parse(args);

        cliAssert(commandLine).hasOption(CliOption.PULL_REQUEST_ID.getCommandLineParam()).withValue(SAMPLE_PULL_REQUEST_ID);
    }

    private String[] toArgs(String argsFormat, String... substitutions) {
        return String.format(argsFormat, (Object[]) substitutions).split(" ");
    }

    private CliAssert cliAssert(CommandLine cli) {
        return new CliAssert(cli);
    }

    private static class CliAssert {

        CommandLine object;
        private String optionName;

        public CliAssert(CommandLine object) {
            this.object = object;
        }

        public CliAssert hasOption(String optionName) {
            assertThat(object.hasOption(optionName)).isTrue();
            this.optionName = optionName;
            return this;
        }

        public CliAssert withValue(String value) {
            assertThat(optionName).withFailMessage("Call hasOption first!").isNotNull();
            assertThat(object.getOptionValue(optionName)).isEqualTo(value);
            return this;
        }
    }
}
