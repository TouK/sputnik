package pl.touk.sputnik.review.locator;

public class MavenBuildDirLocator extends AbstractBuildDirLocator {

    public MavenBuildDirLocator(String sourceDir, String testDir) {
	super(sourceDir, testDir);
    }

    @Override
    protected String getMainBuildDir() {
        return "target/classes";
    }

    @Override
    protected String getTestBuildDir() {
        return "target/test-classes";
    }
}
