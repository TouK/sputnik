package pl.touk.sputnik.review.locator;

import pl.touk.sputnik.build.BuildTool;
import pl.touk.sputnik.configuration.ConfigurationHolder;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.configuration.GeneralOptionNotSupportedException;

public class BuildDirLocatorFactory {

    public static BuildDirLocator create() {
        String buildTool = ConfigurationHolder.instance().getProperty(GeneralOption.BUILD_TOOL);
        if (BuildTool.MAVEN.getName().equals(buildTool)) {
            return new MavenBuildDirLocator();
        } else if (BuildTool.GRADLE.getName().equals(buildTool)) {
            return new GradleBuildFileLocator();
        }
        throw new GeneralOptionNotSupportedException("Build tool " + buildTool + " not supported");
    }

}
