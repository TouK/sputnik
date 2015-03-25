package pl.touk.sputnik.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.touk.sputnik.connector.ConnectorType;

@AllArgsConstructor
@Getter
public enum GeneralOption implements ConfigurationOption {

    BUILD_TOOL("project.build.tool", "Build tool: <maven|gradle>", BuildTool.MAVEN.getName()),

    PROCESS_TEST_FILES("global.processTestFiles", "Process test files?", "true"),
    MAX_NUMBER_OF_COMMENTS("global.maxNumberOfComments", "Maximum number of comments to submit", "0"),
    COMMENT_ONLY_CHANGED_LINES("global.commentOnlyChangedLines", "Comment only changed lines and context", "false"),

    MESSAGE_COMMENT_FORMAT("message.commentFormat", "Sputnik comment format. {0}: reporter, {1}: level, {2}: message", "[{0}] {1}: {2}"),
    MESSAGE_PROBLEM_FORMAT("message.problemFormat", "Sputnik problem format. {0}: reporter, {1}: message", "There is a problem with {0}: {1}"),
    MESSAGE_SCORE_PASSING_COMMENT("message.scorePassingComment", "Comment when no errors are found", "Perfect!"),

    CONNECTOR_TYPE("connector.type", "Connector: <stash|gerrit>", ConnectorType.GERRIT.getName()),
    HOST("connector.host", "Connector server host", "localhost"),
    PORT("connector.port", "Connector server port", "80"),
    PATH("connector.path", "Connector server path", ""),
    USE_HTTPS("connector.useHttps", "Connector use https?", "false"),
    USERNAME("connector.username", "Connector server username", "user"),
    PASSWORD("connector.password", "Connector server password", "password"),
    PROJECT_KEY("connector.projectKey", "Connector server projectKey", null),
    REPOSITORY_SLUG("connector.repositorySlug", "Connector server repositorySlug", null),

    SCORE_STRATEGY("score.strategy", "Score strategy: <NoScore|ScoreAlwaysPass|ScorePassIfEmpty|ScorePassIfNoErrors>", "ScoreAlwaysPass"),
    SCORE_PASSING_KEY("score.passingKey", "Score passing key", "Code-Review"),
    SCORE_PASSING_VALUE("score.passingValue", "Score passing value", "1"),
    SCORE_FAILING_KEY("score.failingKey", "Score failing key", "Code-Review"),
    SCORE_FAILING_VALUE("score.failingValue", "Score failing value", "-1"),

    CHECKSTYLE_ENABLED("checkstyle.enabled", "Checkstyle enabled", "false"),
    CHECKSTYLE_CONFIGURATION_FILE("checkstyle.configurationFile", "Checkstyle configuration file", "sun_checks.xml"),

    PMD_ENABLED("pmd.enabled", "PMD enabled", "false"),
    PMD_RULESETS("pmd.ruleSets", "PMD rule sets", "rulesets/java/basic.xml"),
    PMD_SHOW_VIOLATION_DETAILS("pmd.showViolationDetails", "Show violation details and URL", "false"),

    FINDBUGS_ENABLED("findbugs.enabled", "FindBugs enabled", "false"),
    FINDBUGS_INCLUDE_FILTER("findbugs.includeFilter", "FindBugs include filter file", ""),
    FINDBUGS_EXCLUDE_FILTER("findbugs.excludeFilter", "FindBugs exclude filter file", ""),

    SCALASTYLE_ENABLED("scalastyle.enabled", "ScalaStyle enabled", "false"),
    SCALASTYLE_CONFIGURATION_FILE("scalastyle.configurationFile", "ScalaStyle configuration file", ""),

    CODE_NARC_ENABLED("codenarc.enabled", "CodeNarc enabled", "false"),
    CODE_NARC_RULESET("codenarc.ruleSets", "CodeNarc rulesets file liest", "codeNarcBasicRuleset.xml"),
    CODE_NARC_EXCLUDES("codenarc.excludes", "CodeNarc exclude filter", ""),

    JSLINT_ENABLED("jslint.enabled", "JSLint enabled", "false"),
    JSLINT_CONFIGURATION_FILE("jslint.configurationFile", "JSLint configuration file", ""),

    JSHINT_ENABLED("jshint.enabled", "JSHint enabled", "false"),
    JSHINT_CONFIGURATION_FILE("jshint.configurationFile", "JSHint configuration file", ""),

    SONAR_ENABLED("sonar.enabled", "Sonar enabled", "false"),
    SONAR_PROPERTIES("sonar.configurationFiles", "Sonar base configuration", "sonar-project.properties"),
    SONAR_VERBOSE("sonar.verbose", "Run sonar in verbose mode", "false");

    private final String key;
    private final String description;
    private final String defaultValue;
}
