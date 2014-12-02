package pl.touk.sputnik.processor.sonar;

/**
 * Embed sonar configuration properties keys
 *
 */
public class SonarProperties {

    public static final String ANALISYS_MODE = "sonar.analysis.mode";
    public static final String SCM_ENABLED = "sonar.scm.enabled";
    public static final String SCM_STAT_ENABLED = "sonar.scm-stats.enabled";
    public static final String ISSUEASSIGN_PLUGIN = "issueassignplugin.enabled";
    public static final String EXPORT_PATH = "sonar.report.export.path";
    public static final String VERBOSE = "sonar.verbose";
    public static final String INCLUDE_FILES = "sonar.inclusions";
    public static final String WORKDIR = "sonar.working.directory";
    public static final String PROJECT_BASEDIR = "sonar.projectBaseDir";
}
