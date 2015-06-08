package pl.touk.sputnik.processor.tslint;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import pl.touk.sputnik.review.ReviewException;

/**
 * Represents instance of TSLint executable file, which is used for validating files.
 */
@Slf4j
public class TSLintScript {

    /** Name of the NodeJs process. */
    private static final String NODE_JS = "node";
    /** Argument for validating the file. */
    private static final String TS_LINT_FILE_PARAM = "--file";
    /** Argument for validating the file. */
    private static final String TS_LINT_CONFIG_PARAM = "--config";
    /** Determines the output format. */
    private static final String TS_LINT_OUTPUT_KEY = "--format";
    private static final String TS_LINT_OUTPUT_VALUE = "json";

    /** TSLint script that validates files. */
    private final String tsScript;

    /** File with rules. */
    private final String configFile;

    public TSLintScript(String tsScript, String configFile) {
        this.tsScript = tsScript;
        this.configFile = configFile;
    }

    /**
     * Since this class needs to have setup correctly external configuration, we want to validate this configuration
     * before validation starts.
     * 
     * @throws ReviewException
     *             when configuration is not valid or completed
     */
    public void validateConfiguration() throws ReviewException {
        // check if config file exist
        if (!new File(configFile).exists()) {
            throw new ReviewException("Could not find tslint configuraiton file: " + configFile);
        }
    }

    /**
     * Executes TSLint to look for violations.
     * 
     * @param filePath
     *            file that will be examined
     * @return violations in JSON format
     */
    public String reviewFile(String filePath) {
        log.info("Reviewing file: " + filePath);
        // use this format to make sure that ' ' are parsed properly
        String[] commands = new String[] {NODE_JS, tsScript, TS_LINT_OUTPUT_KEY, TS_LINT_OUTPUT_VALUE,
                                          TS_LINT_CONFIG_PARAM, configFile, TS_LINT_FILE_PARAM, filePath };
        return executeTSLint(commands);
    }

    /**
     * Execute TSLint executable file with parameters that will be append to this file.
     * 
     * @param commands
     *            arguments that will be used for TSLint
     * @return results converted to string
     * @throws ReviewException
     *             when TSlint could not be executed
     */
    private String executeTSLint(String[] commands) throws ReviewException {
        try {
            log.info("Running native process: " + StringUtils.join(commands, " "));
            ProcessBuilder builder = new ProcessBuilder(commands);
            Process tsLintProc = builder.start();

            try (BufferedReader stream = new BufferedReader(new InputStreamReader(tsLintProc.getInputStream()))) {
                String jsonOutput = streamToString(stream);
                log.debug("TSLint ends with result: " + jsonOutput);

                return jsonOutput;
            }
        } catch (IOException e) {
            throw new ReviewException("Failed to run tslint by executing: " + StringUtils.join(commands, " "), e);
        }
    }

    private String streamToString(BufferedReader stream) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(stream, writer);

        return writer.toString();
    }
}
