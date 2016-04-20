package pl.touk.sputnik.processor.tslint;

import lombok.extern.slf4j.Slf4j;
import pl.touk.sputnik.exec.ExternalProcess;
import pl.touk.sputnik.review.ReviewException;

import java.io.File;

/**
 * Represents instance of TSLint executable file, which is used for validating files.
 */
@Slf4j
public class TSLintScript {

    /** Name of the NodeJs process. */
    private static final String NODE_JS = "node";
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
            throw new TSLintException("Could not find tslint configuration file: " + configFile);
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
        String[] args = new String[] {NODE_JS, tsScript, TS_LINT_OUTPUT_KEY, TS_LINT_OUTPUT_VALUE,
                                          TS_LINT_CONFIG_PARAM, configFile, filePath };
        return new ExternalProcess().executeCommand(args);
    }

}
