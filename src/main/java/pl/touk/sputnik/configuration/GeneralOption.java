package pl.touk.sputnik.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GeneralOption implements ConfigurationOption {
    PROCESS_TEST_FILES("global.processTestFiles", "Process test files?", "true"),
    MAX_NUMBER_OF_COMMENTS("global.maxNumberOfComments", "Maximum number of comments to submit", "0"),

    CONNECTOR_TYPE("connector.type", "Connector: <stash|gerrit>", "gerrit"),
    HOST("connector.host", "Connector server host", "localhost"),
    PORT("connector.port", "Connector server port", "80"),
    USE_HTTPS("connector.useHttps", "Connector use https?", "false"),
    USERNAME("connector.username", "Connector server username", "user"),
    PASSWORD("connector.password", "Connector server password", "password"),
    PROJECT_KEY("connector.projectKey", "Connector server projectKey", null),
    REPOSITORY_SLUG("connector.repositorySlug", "Connector server repositorySlug", null),

    CHECKSTYLE_ENABLED("checkstyle.enabled", "Checkstyle enabled", "false"),
    CHECKSTYLE_CONFIGURATION_FILE("checkstyle.configurationFile", "Checkstyle configuration file", "sun_checks.xml"),

    PMD_ENABLED("pmd.enabled", "PMD enabled", "false"),
    PMD_RULESETS("pmd.ruleSets", "PMD rule sets", "rulesets/java/basic.xml"),

    FINDBUGS_ENABLED("findbugs.enabled", "FindBugs enabled", "false"),
    FINDBUGS_INCLUDE_FILTER("findbugs.includeFilter", "FindBugs include filter file", ""),
    FINDBUGS_EXCLUDE_FILTER("findbugs.excludeFilter", "FindBugs exclude filter file", ""),

    SCALASTYLE_ENABLED("scalastyle.enabled", "ScalaStyle enabled", "false"),
    SCALASTYLE_CONFIGURATION_FILE("scalastyle.configurationFile", "ScalaStyle configuration file", "");

    private String key;
    private String description;
    private String defaultValue;
}
