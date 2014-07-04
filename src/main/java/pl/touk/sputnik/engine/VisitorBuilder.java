package pl.touk.sputnik.engine;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.ConfigurationHolder;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.engine.visitor.*;

import java.util.ArrayList;
import java.util.List;

public class VisitorBuilder {

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

        afterReviewVisitors.add(new StaticScoreVisitor(ImmutableMap.of("Code-Review", 1)));

        return afterReviewVisitors;
    }
}
