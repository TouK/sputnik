package pl.touk.sputnik.engine;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.touk.sputnik.configuration.CliOption;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationSetup;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.connector.ConnectorFacade;
import pl.touk.sputnik.connector.gerrit.GerritFacade;
import pl.touk.sputnik.engine.visitor.AfterReviewVisitor;
import pl.touk.sputnik.engine.visitor.FilterOutTestFilesVisitor;
import pl.touk.sputnik.engine.visitor.LimitCommentVisitor;
import pl.touk.sputnik.engine.visitor.RegexFilterFilesVisitor;
import pl.touk.sputnik.engine.visitor.SummaryMessageVisitor;
import pl.touk.sputnik.engine.visitor.comment.GerritCommentVisitor;
import pl.touk.sputnik.engine.visitor.score.NoScore;
import pl.touk.sputnik.engine.visitor.score.ScoreAlwaysPass;
import pl.touk.sputnik.engine.visitor.score.ScorePassIfEmpty;
import pl.touk.sputnik.engine.visitor.score.ScorePassIfNoErrors;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class VisitorBuilderTest {

    @Mock
    private ConnectorFacade connectorFacade;

    @Test
    void shouldNotBuildBeforeVisitors() {
        Configuration config = new ConfigurationSetup().setUp(Collections.<String, String>emptyMap());

        assertThat(new VisitorBuilder().buildBeforeReviewVisitors(config)).isEmpty();
    }

    @Test
    void shouldNotBuildDisabledBeforeVisitors() {
        Configuration config = new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.PROCESS_TEST_FILES.getKey(), "true"
        ));

        assertThat(new VisitorBuilder().buildBeforeReviewVisitors(config)).isEmpty();
    }

    @Test
    void shouldBuildBeforeVisitors() {
        Configuration config = new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.PROCESS_TEST_FILES.getKey(), "false"
        ));

        assertThat(new VisitorBuilder().buildBeforeReviewVisitors(config))
                .hasSize(1)
                .extracting("class")
                .containsExactly(FilterOutTestFilesVisitor.class);
    }

    @Test
    void shouldAddRegexFilterToBeforeVisitorsWhenConfigured() {
        Configuration config = new ConfigurationSetup().setUp(ImmutableMap.of(
                CliOption.FILE_REGEX.getKey(), "^myModule/.+"
        ));

        assertThat(new VisitorBuilder().buildBeforeReviewVisitors(config))
                .hasSize(1)
                .extracting("class")
                .containsExactly(RegexFilterFilesVisitor.class);
    }

    @Test
    void shouldBuildAfterVisitors() {
        Configuration config = new ConfigurationSetup().setUp(Collections.<String, String>emptyMap());

        assertThat(new VisitorBuilder().buildAfterReviewVisitors(config, connectorFacade))
                .hasSize(2)
                .extracting("class")
                .containsExactly(SummaryMessageVisitor.class, ScoreAlwaysPass.class);
    }

    @Test
    void shouldNotBuildDisabledAfterVisitors() {
        Configuration config = new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.MAX_NUMBER_OF_COMMENTS.getKey(), "0"
        ));

        assertThat(new VisitorBuilder().buildAfterReviewVisitors(config, connectorFacade))
                .hasSize(2)
                .extracting("class")
                .containsExactly(SummaryMessageVisitor.class, ScoreAlwaysPass.class);
    }

    @Test
    void shouldBuildFilterOutCommentVisitor() {
        Configuration config = new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.MAX_NUMBER_OF_COMMENTS.getKey(), "50"
        ));

        assertThat(new VisitorBuilder().buildAfterReviewVisitors(config, connectorFacade))
                .hasSize(3)
                .extracting("class")
                .containsExactly(SummaryMessageVisitor.class, LimitCommentVisitor.class, ScoreAlwaysPass.class);
    }

    @Test
    void shouldBuildCommentVisitorForGerrit() {
        connectorFacade = mock(GerritFacade.class);
        Configuration config = new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.COMMENT_ONLY_CHANGED_LINES.getKey(), "true"
        ));

        assertThat(new VisitorBuilder().buildAfterReviewVisitors(config, connectorFacade))
                .hasSize(3)
                .extracting("class")
                .containsExactly(GerritCommentVisitor.class, SummaryMessageVisitor.class, ScoreAlwaysPass.class);
    }

    @Test
    void shouldBuildNoScoreVisitor() {
        Configuration config = new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.SCORE_STRATEGY.getKey(), "NOscore"
        ));

        assertThat(new VisitorBuilder().buildAfterReviewVisitors(config, connectorFacade))
                .hasSize(2)
                .extracting("class")
                .containsExactly(SummaryMessageVisitor.class, NoScore.class);
    }

    @Test
    void shouldBuildScoreAlwaysPassVisitor() {
        Configuration config = new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.SCORE_STRATEGY.getKey(), "scoreAlwaysPass",
                GeneralOption.SCORE_PASSING_KEY.getKey(), "Verified",
                GeneralOption.SCORE_PASSING_VALUE.getKey(), "2"
        ));

        List<AfterReviewVisitor> afterReviewVisitors = new VisitorBuilder().buildAfterReviewVisitors(config, connectorFacade);

        assertThat(afterReviewVisitors)
                .hasSize(2)
                .extracting("class")
                .containsExactly(SummaryMessageVisitor.class, ScoreAlwaysPass.class);
        assertThat(((ScoreAlwaysPass) afterReviewVisitors.get(1)).getPassingScore()).containsOnly(entry("Verified", (short) 2));
    }

    @Test
    void shouldBuildScorePassIfEmptyVisitor() {
        Configuration config = new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.SCORE_STRATEGY.getKey(), "SCOREPASSIFEMPTY",
                GeneralOption.SCORE_PASSING_KEY.getKey(), "Verified",
                GeneralOption.SCORE_PASSING_VALUE.getKey(), "3",
                GeneralOption.SCORE_FAILING_KEY.getKey(), "Verified",
                GeneralOption.SCORE_FAILING_VALUE.getKey(), "-3"
        ));

        List<AfterReviewVisitor> afterReviewVisitors = new VisitorBuilder().buildAfterReviewVisitors(config, connectorFacade);

        assertThat(afterReviewVisitors)
                .hasSize(2)
                .extracting("class")
                .containsExactly(SummaryMessageVisitor.class, ScorePassIfEmpty.class);
        assertThat(((ScorePassIfEmpty) afterReviewVisitors.get(1)).getPassingScore()).containsOnly(entry("Verified", (short) 3));
        assertThat(((ScorePassIfEmpty) afterReviewVisitors.get(1)).getFailingScore()).containsOnly(entry("Verified", (short) -3));
    }

    @Test
    void shouldBuildScorePassIfNoErrorsVisitor() {
        Configuration config = new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.SCORE_STRATEGY.getKey(), "SCOREPassIfNoErrors",
                GeneralOption.SCORE_PASSING_KEY.getKey(), "Code-Review",
                GeneralOption.SCORE_PASSING_VALUE.getKey(), "1",
                GeneralOption.SCORE_FAILING_KEY.getKey(), "Code-Review",
                GeneralOption.SCORE_FAILING_VALUE.getKey(), "-2"
        ));

        List<AfterReviewVisitor> afterReviewVisitors = new VisitorBuilder().buildAfterReviewVisitors(config, connectorFacade);

        assertThat(afterReviewVisitors)
                .hasSize(2)
                .extracting("class")
                .containsExactly(SummaryMessageVisitor.class, ScorePassIfNoErrors.class);
        assertThat(((ScorePassIfNoErrors) afterReviewVisitors.get(1)).getPassingScore()).containsOnly(entry("Code-Review", (short) 1));
        assertThat(((ScorePassIfNoErrors) afterReviewVisitors.get(1)).getFailingScore()).containsOnly(entry("Code-Review", (short) -2));
    }

    @Test
    void shouldBuildDefaultScoreAlwaysPassIfStrategyIsUnknown() {
        Configuration config = new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.SCORE_STRATEGY.getKey(), "mySimpleStrategy"
        ));

        List<AfterReviewVisitor> afterReviewVisitors = new VisitorBuilder().buildAfterReviewVisitors(config, connectorFacade);

        assertThat(afterReviewVisitors)
                .hasSize(2)
                .extracting("class")
                .containsExactly(SummaryMessageVisitor.class, ScoreAlwaysPass.class);
        assertThat(((ScoreAlwaysPass) afterReviewVisitors.get(1)).getPassingScore()).containsOnly(entry("Code-Review", (short) 1));
    }

}