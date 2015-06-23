package pl.touk.sputnik.engine;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import pl.touk.sputnik.configuration.Configuration;
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

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;

public class VisitorBuilderTest {

    @Test
    public void shouldNotBuildBeforeVisitors() {
        Configuration config = new ConfigurationSetup().setUp(Collections.<String, String>emptyMap());

        assertThat(new VisitorBuilder().buildBeforeReviewVisitors(config)).isEmpty();
    }

    @Test
    public void shouldNotBuildDisabledBeforeVisitors() {
        Configuration config = new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.PROCESS_TEST_FILES.getKey(), "true"
        ));

        assertThat(new VisitorBuilder().buildBeforeReviewVisitors(config)).isEmpty();
    }

    @Test
    public void shouldBuildBeforeVisitors() {
        Configuration config = new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.PROCESS_TEST_FILES.getKey(), "false"
        ));

        assertThat(new VisitorBuilder().buildBeforeReviewVisitors(config))
                .hasSize(1)
                .extracting("class")
                .containsExactly(FilterOutTestFilesVisitor.class);
    }

    @Test
    public void shouldBuildAfterVisitors() {
        Configuration config = new ConfigurationSetup().setUp(Collections.<String, String>emptyMap());

        assertThat(new VisitorBuilder().buildAfterReviewVisitors(config))
                .hasSize(2)
                .extracting("class")
                .containsExactly(SummaryMessageVisitor.class, ScoreAlwaysPass.class);
    }

    @Test
    public void shouldNotBuildDisabledAfterVisitors() {
        Configuration config = new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.MAX_NUMBER_OF_COMMENTS.getKey(), "0"
        ));

        assertThat(new VisitorBuilder().buildAfterReviewVisitors(config))
                .hasSize(2)
                .extracting("class")
                .containsExactly(SummaryMessageVisitor.class, ScoreAlwaysPass.class);
    }

    @Test
    public void shouldBuildFilterOutCommentVisitor() {
        Configuration config = new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.MAX_NUMBER_OF_COMMENTS.getKey(), "50"
        ));

        assertThat(new VisitorBuilder().buildAfterReviewVisitors(config))
                .hasSize(3)
                .extracting("class")
                .containsExactly(SummaryMessageVisitor.class, LimitCommentVisitor.class, ScoreAlwaysPass.class);
    }

    @Test
    public void shouldBuildNoScoreVisitor() {
        Configuration config = new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.SCORE_STRATEGY.getKey(), "NOscore"
        ));

        assertThat(new VisitorBuilder().buildAfterReviewVisitors(config))
                .hasSize(2)
                .extracting("class")
                .containsExactly(SummaryMessageVisitor.class, NoScore.class);
    }

    @Test
    public void shouldBuildScoreAlwaysPassVisitor() {
        Configuration config = new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.SCORE_STRATEGY.getKey(), "scoreAlwaysPass",
                GeneralOption.SCORE_PASSING_KEY.getKey(), "Verified",
                GeneralOption.SCORE_PASSING_VALUE.getKey(), "2"
        ));

        List<AfterReviewVisitor> afterReviewVisitors = new VisitorBuilder().buildAfterReviewVisitors(config);

        assertThat(afterReviewVisitors)
                .hasSize(2)
                .extracting("class")
                .containsExactly(SummaryMessageVisitor.class, ScoreAlwaysPass.class);
        assertThat(((ScoreAlwaysPass) afterReviewVisitors.get(1)).getPassingScore()).containsOnly(entry("Verified", (short) 2));
    }

    @Test
    public void shouldBuildScorePassIfEmptyVisitor() {
        Configuration config = new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.SCORE_STRATEGY.getKey(), "SCOREPASSIFEMPTY",
                GeneralOption.SCORE_PASSING_KEY.getKey(), "Verified",
                GeneralOption.SCORE_PASSING_VALUE.getKey(), "3",
                GeneralOption.SCORE_FAILING_KEY.getKey(), "Verified",
                GeneralOption.SCORE_FAILING_VALUE.getKey(), "-3"
        ));

        List<AfterReviewVisitor> afterReviewVisitors = new VisitorBuilder().buildAfterReviewVisitors(config);

        assertThat(afterReviewVisitors)
                .hasSize(2)
                .extracting("class")
                .containsExactly(SummaryMessageVisitor.class, ScorePassIfEmpty.class);
        assertThat(((ScorePassIfEmpty) afterReviewVisitors.get(1)).getPassingScore()).containsOnly(entry("Verified", (short) 3));
        assertThat(((ScorePassIfEmpty) afterReviewVisitors.get(1)).getFailingScore()).containsOnly(entry("Verified", (short) -3));
    }

    @Test
    public void shouldBuildScorePassIfNoErrorsVisitor() {
        Configuration config = new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.SCORE_STRATEGY.getKey(), "SCOREPassIfNoErrors",
                GeneralOption.SCORE_PASSING_KEY.getKey(), "Code-Review",
                GeneralOption.SCORE_PASSING_VALUE.getKey(), "1",
                GeneralOption.SCORE_FAILING_KEY.getKey(), "Code-Review",
                GeneralOption.SCORE_FAILING_VALUE.getKey(), "-2"
        ));

        List<AfterReviewVisitor> afterReviewVisitors = new VisitorBuilder().buildAfterReviewVisitors(config);

        assertThat(afterReviewVisitors)
                .hasSize(2)
                .extracting("class")
                .containsExactly(SummaryMessageVisitor.class, ScorePassIfNoErrors.class);
        assertThat(((ScorePassIfNoErrors) afterReviewVisitors.get(1)).getPassingScore()).containsOnly(entry("Code-Review", (short) 1));
        assertThat(((ScorePassIfNoErrors) afterReviewVisitors.get(1)).getFailingScore()).containsOnly(entry("Code-Review", (short) -2));
    }

    @Test
    public void shouldBuildDefaultScoreAlwaysPassIfStrategyIsUnknown() {
        Configuration config = new ConfigurationSetup().setUp(ImmutableMap.of(
                GeneralOption.SCORE_STRATEGY.getKey(), "mySimpleStrategy"
        ));

        List<AfterReviewVisitor> afterReviewVisitors = new VisitorBuilder().buildAfterReviewVisitors(config);

        assertThat(afterReviewVisitors)
                .hasSize(2)
                .extracting("class")
                .containsExactly(SummaryMessageVisitor.class, ScoreAlwaysPass.class);
        assertThat(((ScoreAlwaysPass) afterReviewVisitors.get(1)).getPassingScore()).containsOnly(entry("Code-Review", (short) 1));
    }

}