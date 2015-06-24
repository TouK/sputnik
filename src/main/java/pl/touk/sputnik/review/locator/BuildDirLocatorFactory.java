package pl.touk.sputnik.review.locator;

import lombok.extern.slf4j.Slf4j;
import pl.touk.sputnik.configuration.BuildTool;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;

import static org.apache.commons.lang3.Validate.notBlank;

@Slf4j
public class BuildDirLocatorFactory {

    public static BuildDirLocator create(Configuration configuration) {
        String buildTool = configuration.getProperty(GeneralOption.BUILD_TOOL);
        notBlank(buildTool);

        switch (BuildTool.valueOf(buildTool.toUpperCase())) {
            case MAVEN:
                return new MavenBuildDirLocator();
            case GRADLE:
                return new GradleBuildFileLocator();
            default:
                log.warn("Build tool " + buildTool + " not supported, using maven");
                return new MavenBuildDirLocator();
        }
    }

}
