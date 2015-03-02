package pl.touk.sputnik.review.locator;

public class MavenBuildDirLocator extends AbstractBuildDirLocator {

    @Override
    protected String getMainBuildDir() {
        return "target/classes";
    }

    @Override
    protected String getTestBuildDir() {
        return "target/test-classes";
    }
}
