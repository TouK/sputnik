package pl.touk.sputnik.engine;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.jetbrains.annotations.NotNull;

import pl.touk.sputnik.configuration.ConfigurationHolder;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.engine.visitor.AfterReviewVisitor;
import pl.touk.sputnik.engine.visitor.BeforeReviewVisitor;
import pl.touk.sputnik.engine.visitor.FilterOutTestFilesVisitor;
import pl.touk.sputnik.engine.visitor.LimitCommentVisitor;
import pl.touk.sputnik.engine.visitor.SummaryMessageVisitor;
import pl.touk.sputnik.engine.visitor.score.NoScore;
import pl.touk.sputnik.engine.visitor.score.ScoreAlwaysPass;
import pl.touk.sputnik.engine.visitor.score.ScorePassIfEmpty;
import pl.touk.sputnik.engine.visitor.score.ScorePassIfNoErrors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class VisitorBuilder {

    public enum ScoreStrategies {
        NoScore,
        ScoreAlwaysPass,
        ScorePassIfEmpty,
        ScorePassIfNoErrors;
    }

    @NotNull
    public List<BeforeReviewVisitor> buildBeforeReviewVisitors() {
        List<BeforeReviewVisitor> beforeReviewVisitors = new ArrayList<>();
        if (!BooleanUtils.toBoolean(ConfigurationHolder.instance().getProperty(GeneralOption.PROCESS_TEST_FILES))) {
            beforeReviewVisitors.add(new FilterOutTestFilesVisitor());
        }
        return beforeReviewVisitors;
    }

    @NotNull
    public List<AfterReviewVisitor> buildAfterReviewVisitors() {
        List<AfterReviewVisitor> afterReviewVisitors = new ArrayList<>();

        afterReviewVisitors.add(new SummaryMessageVisitor());

        int maxNumberOfComments = NumberUtils.toInt(ConfigurationHolder.instance().getProperty(GeneralOption.MAX_NUMBER_OF_COMMENTS), 0);
        if (maxNumberOfComments > 0) {
            afterReviewVisitors.add(new LimitCommentVisitor(maxNumberOfComments));
        }

        afterReviewVisitors.add(buildScoreAfterReviewVisitor());

        return afterReviewVisitors;
    }

    @NotNull
    private AfterReviewVisitor buildScoreAfterReviewVisitor() {
        Map<String, Integer> passingScore = ImmutableMap.<String, Integer>of(
                ConfigurationHolder.instance().getProperty(GeneralOption.SCORE_PASSING_KEY),
                Integer.valueOf(ConfigurationHolder.instance().getProperty(GeneralOption.SCORE_PASSING_VALUE))
        );
        Map<String, Integer> failingScore = ImmutableMap.<String, Integer>of(
                ConfigurationHolder.instance().getProperty(GeneralOption.SCORE_FAILING_KEY),
                Integer.valueOf(ConfigurationHolder.instance().getProperty(GeneralOption.SCORE_FAILING_VALUE))
        );
        ScoreStrategies score = ScoreStrategies.valueOf(ConfigurationHolder.instance().getProperty(
                GeneralOption.SCORE_STRATEGY));

        switch (score) {
            case NoScore:
                return new NoScore();

            case ScoreAlwaysPass:
                return new ScoreAlwaysPass(passingScore);

            case ScorePassIfEmpty:
                return new ScorePassIfEmpty(passingScore, failingScore);

            case ScorePassIfNoErrors:
                return new ScorePassIfNoErrors(passingScore, failingScore);

            default:
                throw new IllegalArgumentException("Unknown strategy: " + score);
        }
    }
}
