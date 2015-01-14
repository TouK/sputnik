package pl.touk.sputnik.engine;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import pl.touk.sputnik.configuration.ConfigurationSetup;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.engine.visitor.AfterReviewVisitor;
import pl.touk.sputnik.engine.visitor.FilterOutTestFilesVisitor;
import pl.touk.sputnik.engine.visitor.LimitCommentVisitor;
import pl.touk.sputnik.engine.visitor.SummaryMessageVisitor;
import pl.touk.sputnik.engine.visitor.score.NoScore;
import pl.touk.sputnik.engine.visitor.score.ScoreAlwaysPass;
import pl.touk.sputnik.engine.visitor.score.ScorePassIfEmpty;
import pl.touk.sputnik.engine.visitor.score.ScorePassIfNoErrors;
import pl.touk.sputnik.engine.visitor.score.ScoreStrategies;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;

public class VisitorBuilderTest {

    @Test
    public void shouldNotBuildBeforeVisitors() {
        new ConfigurationSetup().setUp(Collections.<String, String>emptyMap());

        assertThat(new VisitorBuilder().buildBeforeReviewVisitors()).isEmpty();
    }

    @Test
    public void shouldNotBuildDisabledBeforeVisitors() {
        new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.PROCESS_TEST_FILES.getKey(), "true"
        ));

        assertThat(new VisitorBuilder().buildBeforeReviewVisitors()).isEmpty();
    }

    @Test
    public void shouldBuildBeforeVisitors() {
        new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.PROCESS_TEST_FILES.getKey(), "false"
        ));

        assertThat(new VisitorBuilder().buildBeforeReviewVisitors())
                .hasSize(1)
                .extracting("class")
                .containsExactly(FilterOutTestFilesVisitor.class);
    }

    @Test
    public void shouldBuildAfterVisitors() {
        new ConfigurationSetup().setUp(Collections.<String, String>emptyMap());

        assertThat(new VisitorBuilder().buildAfterReviewVisitors())
                .hasSize(2)
                .extracting("class")
                .containsExactly(SummaryMessageVisitor.class, ScoreAlwaysPass.class);
    }

    @Test
    public void shouldNotBuildDisabledAfterVisitors() {
        new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.MAX_NUMBER_OF_COMMENTS.getKey(), "0"
        ));

        assertThat(new VisitorBuilder().buildAfterReviewVisitors())
                .hasSize(2)
                .extracting("class")
                .containsExactly(SummaryMessageVisitor.class, ScoreAlwaysPass.class);
    }

    @Test
    public void shouldBuildFilterOutCommentVisitor() {
        new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.MAX_NUMBER_OF_COMMENTS.getKey(), "50"
        ));

        assertThat(new VisitorBuilder().buildAfterReviewVisitors())
                .hasSize(3)
                .extracting("class")
                .containsExactly(SummaryMessageVisitor.class, LimitCommentVisitor.class, ScoreAlwaysPass.class);
    }

    @Test
    public void shouldBuildNoScoreVisitor() {
        new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.SCORE_STRATEGY.getKey(), ScoreStrategies.NoScore.name()
        ));

        assertThat(new VisitorBuilder().buildAfterReviewVisitors())
                .hasSize(2)
                .extracting("class")
                .containsExactly(SummaryMessageVisitor.class, NoScore.class);
    }

    @Test
    public void shouldBuildScoreAlwaysPassVisitor() {
        new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.SCORE_STRATEGY.getKey(), ScoreStrategies.ScoreAlwaysPass.name(),
                GeneralOption.SCORE_PASSING_KEY.getKey(), "Verified",
                GeneralOption.SCORE_PASSING_VALUE.getKey(), "2"
        ));

        List<AfterReviewVisitor> afterReviewVisitors = new VisitorBuilder().buildAfterReviewVisitors();

        assertThat(afterReviewVisitors)
                .hasSize(2)
                .extracting("class")
                .containsExactly(SummaryMessageVisitor.class, ScoreAlwaysPass.class);
        assertThat(((ScoreAlwaysPass) afterReviewVisitors.get(1)).getPassingScore()).containsOnly(entry("Verified", 2));
    }

    @Test
    public void shouldBuildScorePassIfEmptyVisitor() {
        new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.SCORE_STRATEGY.getKey(), ScoreStrategies.ScorePassIfEmpty.name(),
                GeneralOption.SCORE_PASSING_KEY.getKey(), "Verified",
                GeneralOption.SCORE_PASSING_VALUE.getKey(), "3",
                GeneralOption.SCORE_FAILING_KEY.getKey(), "Verified",
                GeneralOption.SCORE_FAILING_VALUE.getKey(), "-3"
        ));

        List<AfterReviewVisitor> afterReviewVisitors = new VisitorBuilder().buildAfterReviewVisitors();

        assertThat(afterReviewVisitors)
                .hasSize(2)
                .extracting("class")
                .containsExactly(SummaryMessageVisitor.class, ScorePassIfEmpty.class);
        assertThat(((ScorePassIfEmpty) afterReviewVisitors.get(1)).getPassingScore()).containsOnly(entry("Verified", 3));
        assertThat(((ScorePassIfEmpty) afterReviewVisitors.get(1)).getFailingScore()).containsOnly(entry("Verified", -3));
    }

    @Test
    public void shouldBuildScorePassIfNoErrorsVisitor() {
        new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.SCORE_STRATEGY.getKey(), ScoreStrategies.ScorePassIfNoErrors.name(),
                GeneralOption.SCORE_PASSING_KEY.getKey(), "Code-Review",
                GeneralOption.SCORE_PASSING_VALUE.getKey(), "1",
                GeneralOption.SCORE_FAILING_KEY.getKey(), "Code-Review",
                GeneralOption.SCORE_FAILING_VALUE.getKey(), "-2"
        ));

        List<AfterReviewVisitor> afterReviewVisitors = new VisitorBuilder().buildAfterReviewVisitors();

        assertThat(afterReviewVisitors)
                .hasSize(2)
                .extracting("class")
                .containsExactly(SummaryMessageVisitor.class, ScorePassIfNoErrors.class);
        assertThat(((ScorePassIfNoErrors) afterReviewVisitors.get(1)).getPassingScore()).containsOnly(entry("Code-Review", 1));
        assertThat(((ScorePassIfNoErrors) afterReviewVisitors.get(1)).getFailingScore()).containsOnly(entry("Code-Review", -2));
    }
}