package pl.touk.sputnik.engine;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.CliOption;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.connector.ConnectorFacade;
import pl.touk.sputnik.connector.gerrit.GerritFacade;
import pl.touk.sputnik.engine.visitor.AfterReviewVisitor;
import pl.touk.sputnik.engine.visitor.BeforeReviewVisitor;
import pl.touk.sputnik.engine.visitor.FilterOutTestFilesVisitor;
import pl.touk.sputnik.engine.visitor.LimitCommentVisitor;
import pl.touk.sputnik.engine.visitor.RegexFilterFilesVisitor;
import pl.touk.sputnik.engine.visitor.SummaryMessageVisitor;
import pl.touk.sputnik.engine.visitor.comment.GerritCommentVisitor;
import pl.touk.sputnik.engine.visitor.comment.GerritFileDiffBuilder;
import pl.touk.sputnik.engine.visitor.comment.GerritFileDiffBuilderWrapper;
import pl.touk.sputnik.engine.visitor.score.NoScore;
import pl.touk.sputnik.engine.visitor.score.ScoreAlwaysPass;
import pl.touk.sputnik.engine.visitor.score.ScorePassIfEmpty;
import pl.touk.sputnik.engine.visitor.score.ScorePassIfNoErrors;

import javax.annotation.Nonnull;
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
    public List<BeforeReviewVisitor> buildBeforeReviewVisitors(Configuration configuration) {
        List<BeforeReviewVisitor> beforeReviewVisitors = new ArrayList<>();
        addTestFilesFilterIfConfigured(configuration, beforeReviewVisitors);
        addRegexFilterIfConfigured(configuration, beforeReviewVisitors);
        return beforeReviewVisitors;
    }

    private void addTestFilesFilterIfConfigured(Configuration configuration, List<BeforeReviewVisitor> beforeReviewVisitors) {
        if (!BooleanUtils.toBoolean(configuration.getProperty(GeneralOption.PROCESS_TEST_FILES))) {
            beforeReviewVisitors.add(new FilterOutTestFilesVisitor(configuration.getProperty(GeneralOption.JAVA_TEST_DIR)));
        }
    }

    private void addRegexFilterIfConfigured(Configuration configuration, List<BeforeReviewVisitor> beforeReviewVisitors) {
        String fileRegex = configuration.getProperty(CliOption.FILE_REGEX);
        if (fileRegex != null) {
            beforeReviewVisitors.add(new RegexFilterFilesVisitor(fileRegex));
        }
    }

    @NotNull
    public List<AfterReviewVisitor> buildAfterReviewVisitors(@Nonnull Configuration configuration, @Nonnull ConnectorFacade connectorFacade) {
        List<AfterReviewVisitor> afterReviewVisitors = new ArrayList<>();

        addCommentVisitor(afterReviewVisitors, configuration, connectorFacade);

        String passingComment = configuration.getProperty(GeneralOption.MESSAGE_SCORE_PASSING_COMMENT);
        afterReviewVisitors.add(new SummaryMessageVisitor(passingComment));

        int maxNumberOfComments = NumberUtils.toInt(configuration.getProperty(GeneralOption.MAX_NUMBER_OF_COMMENTS), 0);
        if (maxNumberOfComments > 0) {
            afterReviewVisitors.add(new LimitCommentVisitor(maxNumberOfComments));
        }

        afterReviewVisitors.add(buildScoreAfterReviewVisitor(configuration));

        return afterReviewVisitors;
    }

    private void addCommentVisitor(List<AfterReviewVisitor> afterReviewVisitors, @Nonnull Configuration configuration, @Nonnull ConnectorFacade connectorFacade) {
        boolean commentOnlyChangedLines = BooleanUtils.toBoolean(configuration.getProperty(GeneralOption.COMMENT_ONLY_CHANGED_LINES));
        if (!commentOnlyChangedLines) {
            return;
        }

        //It's only supported for Gerrit this way
        if (connectorFacade instanceof GerritFacade) {
            afterReviewVisitors.add(new GerritCommentVisitor(
                    new GerritFileDiffBuilderWrapper((GerritFacade) connectorFacade, new GerritFileDiffBuilder())));
        }
    }

    @NotNull
    private AfterReviewVisitor buildScoreAfterReviewVisitor(Configuration configuration) {
        Map<String, Short> passingScore = ImmutableMap.<String, Short>of(
                configuration.getProperty(GeneralOption.SCORE_PASSING_KEY),
                Short.valueOf(configuration.getProperty(GeneralOption.SCORE_PASSING_VALUE))
        );
        Map<String, Short> failingScore = ImmutableMap.<String, Short>of(
                configuration.getProperty(GeneralOption.SCORE_FAILING_KEY),
                Short.valueOf(configuration.getProperty(GeneralOption.SCORE_FAILING_VALUE))
        );
        String scoreStrategy = configuration.getProperty(GeneralOption.SCORE_STRATEGY);
        notBlank(scoreStrategy);

        log.info("Using score strategy {}", scoreStrategy);

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
