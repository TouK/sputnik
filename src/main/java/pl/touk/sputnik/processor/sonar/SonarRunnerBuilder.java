package pl.touk.sputnik.processor.sonar;

import java.util.List;

import org.sonar.runner.api.EmbeddedRunner;

import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;

import com.google.common.collect.Lists;

class SonarRunnerBuilder {
    public SonarRunner prepareRunner(Review review) {
        List<String> files = Lists.newArrayList();
        for (ReviewFile file : review.getFiles()) {
            files.add(file.getReviewFilename());
        }
        SonarRunner sonarRunner = new SonarRunner(files, EmbeddedRunner.create());
        return sonarRunner;
    }
}
