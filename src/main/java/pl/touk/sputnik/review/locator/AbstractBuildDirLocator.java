package pl.touk.sputnik.review.locator;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Nullable;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

@AllArgsConstructor
public abstract class AbstractBuildDirLocator implements BuildDirLocator {

	private final String sourceDir;
	private final String testDir;

	public List<String> getBuildDirs(Review review) {
        return Lists.transform(review.getFiles(), new Function<ReviewFile, String>() {
            @Nullable
            @Override
            public String apply(ReviewFile file) {
                if (file.getReviewFilename().contains(sourceDir)) {
                    return substringBeforeLast(file.getReviewFilename(), sourceDir).concat(getMainBuildDir());
                }
                if (file.getReviewFilename().contains(testDir)) {
                    return substringBeforeLast(file.getReviewFilename(), testDir).concat(getTestBuildDir());
                }
                return file.getSourceDir();
            }
        });
    }

    protected abstract String getMainBuildDir();

    protected abstract String getTestBuildDir();
}
