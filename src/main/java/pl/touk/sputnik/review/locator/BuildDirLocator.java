package pl.touk.sputnik.review.locator;

import pl.touk.sputnik.review.Review;

import java.util.List;

public interface BuildDirLocator {
    List<String> getBuildDirs(Review review);
}
