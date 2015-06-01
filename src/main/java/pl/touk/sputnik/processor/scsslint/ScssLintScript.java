package pl.touk.sputnik.processor.scsslint;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import pl.touk.sputnik.configuration.ConfigurationHolder;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.review.ReviewException;

/**
 * Represents instance of scss-lint executable file, which is used for validating files.
 */
@Slf4j
public class ScssLintScript {

    /** Name of the scss-lint executable file. */
    private static final String SCSS_EXEC = ConfigurationHolder.instance().getProperty(GeneralOption.SCSSLINT_SCRIPT);
    /** Argument for validating the file. */
    private static final String LINT_CONFIG_PARAM = "--config";
    /** Determines the output format. */
    private static final String LINT_OUTPUT_KEY = "--format";
    private static final String LINT_OUTPUT_VALUE = "JSON";

    /** File with rules. */
    private final String configFile;

    public ScssLintScript(String configFile) {
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
            throw new ReviewException("Could not find scss-lint configuraiton file: " + configFile);
        }
    }

    /**
     * Executes scss-lint to look for violations.
     * 
     * @param filePath
     *            file that will be examined
     * @return violations in JSON format
     */
    public String reviewFile(String filePath) {
        log.info("Reviewing file: " + filePath);
        // use this format to make sure that ' ' are parsed properly
        String[] commands = new String[] {
                SCSS_EXEC, LINT_OUTPUT_KEY, LINT_OUTPUT_VALUE, LINT_CONFIG_PARAM, configFile, filePath };
        return executeScssLint(commands);
    }

    /**
     * Execute scss-lLint executable file with parameters that will be append to this file.
     * 
     * @param commands
     *            arguments that will be used for scss-lint
     * @return results converted to string
     * @throws ReviewException
     *             when scss-lint could not be executed
     */
    private String executeScssLint(String[] commands) throws ReviewException {
        try {
            log.info("Running native process: " + StringUtils.join(commands, " "));
            ProcessBuilder builder = new ProcessBuilder(commands);
            Process lintProc = builder.start();

            try (BufferedReader stream = new BufferedReader(new InputStreamReader(lintProc.getInputStream()))) {
                String jsonOutput = streamToString(stream);
                log.debug("scss-lint ends with result: " + jsonOutput);

                return jsonOutput;
            }
        } catch (IOException e) {
            throw new ReviewException("Failed to run scss-lint by executing: " + StringUtils.join(commands, " "), e);
        }
    }

    private String streamToString(BufferedReader stream) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(stream, writer);

        return writer.toString();
    }
}