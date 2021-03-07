package pl.touk.sputnik.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.touk.sputnik.connector.ConnectorType;
import pl.touk.sputnik.review.Paths;

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

    CONNECTOR_TYPE("connector.type", "Connector: <stash|gerrit|github|saas|local>", ConnectorType.GERRIT.getName()),
    HOST("connector.host", "Connector server host", "localhost"),
    PORT("connector.port", "Connector server port", "80"),
    PATH("connector.path", "Connector server path", ""),
    USE_HTTPS("connector.useHttps", "Connector use https?", "false"),
    USERNAME("connector.username", "Connector server username", "user"),
    PASSWORD("connector.password", "Connector server password", "password"),
    PROJECT("connector.project", "Connector server project", null),
    REPOSITORY("connector.repository", "Connector server repository", null),
    VERIFY_SSL("connector.verifySsl", "Verify SSL trust and hostname?", "false"),
    TAG("connector.tag", "Review tag", "sputnik"),

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
    FINDBUGS_LOAD_PROPERTIES_FROM("findbugs.loadPropertiesFrom", "FindBugs properties file", ""),
    FINDBUGS_INCLUDE_FILTER("findbugs.includeFilter", "FindBugs include filter file", ""),
    FINDBUGS_EXCLUDE_FILTER("findbugs.excludeFilter", "FindBugs exclude filter file", ""),

    SPOTBUGS_ENABLED("spotbugs.enabled", "SpotBugs enabled", "false"),
    SPOTBUGS_LOAD_PROPERTIES_FROM("spotbugs.loadPropertiesFrom", "SpotBugs properties file", ""),
    SPOTBUGS_INCLUDE_FILTER("spotbugs.includeFilter", "SpotBugs include filter file", ""),
    SPOTBUGS_EXCLUDE_FILTER("spotbugs.excludeFilter", "SpotBugs exclude filter file", ""),

    SCALASTYLE_ENABLED("scalastyle.enabled", "ScalaStyle enabled", "false"),
    SCALASTYLE_CONFIGURATION_FILE("scalastyle.configurationFile", "ScalaStyle configuration file", ""),

    CODE_NARC_ENABLED("codenarc.enabled", "CodeNarc enabled", "false"),
    CODE_NARC_RULESET("codenarc.ruleSets", "CodeNarc rulesets file list", "codeNarcBasicRuleset.xml"),
    CODE_NARC_EXCLUDES("codenarc.excludes", "CodeNarc exclude filter", ""),

    JSLINT_ENABLED("jslint.enabled", "JSLint enabled", "false"),
    JSLINT_CONFIGURATION_FILE("jslint.configurationFile", "JSLint configuration file", ""),

    JSHINT_ENABLED("jshint.enabled", "JSHint enabled", "false"),
    JSHINT_CONFIGURATION_FILE("jshint.configurationFile", "JSHint configuration file", ""),

    TSLINT_ENABLED("tslint.enabled", "TSLint enabled", "false"),
    TSLINT_CONFIGURATION_FILE("tslint.configurationFile", "TSLint configuration file", "tslint.json"),
    TSLINT_SCRIPT("tslint.script", "TSLint script for validating files", "/usr/bin/tslint"),

    ESLINT_ENABLED("eslint.enabled", "ESLint enabled", "false"),
    ESLINT_RCFILE("eslint.rcfile", "ESLint rcfile", null),
    ESLINT_EXECUTABLE("eslint.executable", "ESLint executable", "eslint"),
    ESLINT_PLUGINS_FOLDER("eslint.pluginsFolder", "ESLint plugin folder", null),

    PYLINT_ENABLED("pylint.enabled", "Pylint enabled", "false"),
    PYLINT_RCFILE("pylint.rcfile", "Pylint rcfile", null),

    KTLINT_ENABLED("ktlint.enabled", "KtLint enabled", "false"),
    KTLINT_EXCLUDE("ktlint.exclude", "KtLint exclude rules", null),

    GITHUB_API_KEY("github.api.key", "Personal access tokens for Github", ""),

    GERRIT_USE_HTTP_PASSWORD("gerrit.useHttpPassword", "Use Gerrit's internal password token.", "false"),
    GERRIT_OMIT_DUPLICATE_COMMENTS("gerrit.omitDuplicateComments", "Avoid publishing same comments for the same patchset.", "true"),

    JAVA_SRC_DIR("java.src.dir", "Java root source directory", Paths.SRC_MAIN),
    JAVA_TEST_DIR("java.test.dir", "Java root test directory", Paths.SRC_TEST),

    DETEKT_ENABLED("detekt.enabled", "Detekt enabled", "false"),
    DETEKT_CONFIG_FILE("detekt.config.file", "Detekt configuration file location", null),

    SHELLCHECK_ENABLED("shellcheck.enabled", "Shellcheck enabled", "false"),
    SHELLCHECK_EXCLUDE("shellcheck.exclude", "Shellcheck exclude rules", null);

    private final String key;
    private final String description;
    private final String defaultValue;
}
