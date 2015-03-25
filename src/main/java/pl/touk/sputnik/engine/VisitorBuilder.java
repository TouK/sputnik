package pl.touk.sputnik.engine;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.ConfigurationHolder;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.engine.visitor.*;
import pl.touk.sputnik.engine.visitor.score.NoScore;
import pl.touk.sputnik.engine.visitor.score.ScoreAlwaysPass;
import pl.touk.sputnik.engine.visitor.score.ScorePassIfEmpty;
import pl.touk.sputnik.engine.visitor.score.ScorePassIfNoErrors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.Validate.notBlank;

@Slf4j
public class VisitorBuilder {
    private static final String NOSCORE = "NOSCORE";
    private static final String SCOREALWAYSPASS = "SCOREALWAYSPASS";
    private static final String SCOREPASSIFEMPTY = "SCOREPASSIFEMPTY";
    private static final String SCOREPASSIFNOERRORS = "SCOREPASSIFNOERRORS";

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

        String passingComment = ConfigurationHolder.instance().getProperty(GeneralOption.SCORE_PASSING_COMMENT);
        afterReviewVisitors.add(new SummaryMessageVisitor(passingComment));

        int maxNumberOfComments = NumberUtils.toInt(ConfigurationHolder.instance().getProperty(GeneralOption.MAX_NUMBER_OF_COMMENTS), 0);
        if (maxNumberOfComments > 0) {
            afterReviewVisitors.add(new LimitCommentVisitor(maxNumberOfComments));
        }

        afterReviewVisitors.add(buildScoreAfterReviewVisitor());

        return afterReviewVisitors;
    }

    @NotNull
    private AfterReviewVisitor buildScoreAfterReviewVisitor() {
        Map<String, Short> passingScore = ImmutableMap.<String, Short>of(
                ConfigurationHolder.instance().getProperty(GeneralOption.SCORE_PASSING_KEY),
                Short.valueOf(ConfigurationHolder.instance().getProperty(GeneralOption.SCORE_PASSING_VALUE))
        );
        Map<String, Short> failingScore = ImmutableMap.<String, Short>of(
                ConfigurationHolder.instance().getProperty(GeneralOption.SCORE_FAILING_KEY),
                Short.valueOf(ConfigurationHolder.instance().getProperty(GeneralOption.SCORE_FAILING_VALUE))
        );
        String scoreStrategy = ConfigurationHolder.instance().getProperty(GeneralOption.SCORE_STRATEGY);
        notBlank(scoreStrategy);

        switch(scoreStrategy.toUpperCase()) {
            case NOSCORE:
                return new NoScore();

            case SCOREALWAYSPASS:
                return new ScoreAlwaysPass(passingScore);

            case SCOREPASSIFEMPTY:
                return new ScorePassIfEmpty(passingScore, failingScore);

            case SCOREPASSIFNOERRORS:
                return new ScorePassIfNoErrors(passingScore, failingScore);

            default:
                log.warn("Score strategy {} not found, using default ScoreAlwaysPass", scoreStrategy);
                return new ScoreAlwaysPass(passingScore);
        }
    }
}
