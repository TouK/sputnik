package pl.touk.sputnik.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GeneralOption implements ConfigurationOption {
    CHECKSTYLE_ENABLED("checkstyle.enabled", "Checkstyle enabled", "false"),
    CHECKSTYLE_CONFIGURATION_FILE("checkstyle.configurationFile", "Checkstyle configuration file", "sun_checks.xml"),
    PMD_ENABLED("pmd.enabled", "PMD enabled", "false"),
    PMD_RULESETS("pmd.ruleSets", "PMD rule sets", "rulesets/java/basic.xml"),
    FINDBUGS_ENABLED("findbugs.enabled", "FindBugs enabled", "false"),
    FINDBUGS_INCLUDE_FILTER("findbugs.includeFilter", "FindBugs include filter file", ""),
    FINDBUGS_EXCLUDE_FILTER("findbugs.excludeFilter", "FindBugs exclude filter file", ""),
    SCALASTYLE_ENABLED("scalastyle.enabled", "ScalaStyle enabled", "false"),
    SCALASTYLE_CONFIG("scalastyle.config", "ScalaStyle config file", ""),
    PROCESS_TEST_FILES("global.process_test", "Process test files?", "true"),
    MAX_NUMBER_OF_COMMENTS("global.max_number_of_comments", "Maximum number of comments to submit", "50");

    private String key;
    private String description;
    private String defaultValue;
}
