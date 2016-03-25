package pl.touk.sputnik.engine;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.ConfigurationHolder;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.engine.visitor.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.BooleanUtils.toBoolean;
import static pl.touk.sputnik.configuration.GeneralOption.PROCESS_TEST_FILES;

public class VisitorBuilder {

    @NotNull
    public List<BeforeReviewVisitor> buildBeforeReviewVisitors() {
        return !toBoolean(getProperty(PROCESS_TEST_FILES)) ?
                Collections.singletonList(new FilterOutTestFilesVisitor()) :
                Collections.emptyList();
    }

    @NotNull
    public List<AfterReviewVisitor> buildAfterReviewVisitors() {
        final List<AfterReviewVisitor> afterReviewVisitors = new ArrayList<>();

        afterReviewVisitors.add(new SummaryMessageVisitor());

        int maxNumberOfComments = NumberUtils.toInt(getProperty(GeneralOption.MAX_NUMBER_OF_COMMENTS), 0);
        if (maxNumberOfComments > 0) {
            afterReviewVisitors.add(new LimitCommentVisitor(maxNumberOfComments));
        }

        afterReviewVisitors.add(new StaticScoreVisitor(ImmutableMap.of("Code-Review", 1)));

        return afterReviewVisitors;
    }

    private static String getProperty(GeneralOption option) {
        return ConfigurationHolder.instance().getProperty(option);
    }
}
