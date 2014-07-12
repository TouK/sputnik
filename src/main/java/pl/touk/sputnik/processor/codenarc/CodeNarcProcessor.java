package pl.touk.sputnik.processor.codenarc;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewProcessor;
import pl.touk.sputnik.review.ReviewResult;

@Slf4j
public class CodeNarcProcessor implements ReviewProcessor {

    private static final String PROCESSOR_NAME = "CodeNarc";

    @Nullable
    @Override
    public ReviewResult process(@NotNull Review review) {

        return null;
    }

    @NotNull
    @Override
    public String getName() {
        return PROCESSOR_NAME;
    }
}
