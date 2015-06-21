package pl.touk.sputnik.processor.sonar;

import java.io.File;
import java.util.List;

import org.sonar.runner.api.EmbeddedRunner;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;

import com.google.common.collect.Lists;

class SonarRunnerBuilder {
    public SonarRunner prepareRunner(Review review, Configuration configuration) {
        List<String> files = Lists.newArrayList();

        /*
         * Some sonar plugin do no include git path as the source file path (for
         * example, sonar-visual-studio plugin generates filenames that are relative
         * to the module csproj file).
         *
         * Using the file basename and a recursive match allows Sonar to match
         * modified files with their indexed names in Sonar.
         *
         * Although this can generate useless analysis (as some files that are not
         * included in a review may be analysed), this has a limited additional
         * cost considering that few files have the same basename inside a
         * repository.
         */
        for (ReviewFile file : review.getFiles()) {
            files.add("**/" + new File(file.getReviewFilename()).getName());
        }
        SonarRunner sonarRunner = new SonarRunner(files, EmbeddedRunner.create(), configuration);
        return sonarRunner;
    }
}
