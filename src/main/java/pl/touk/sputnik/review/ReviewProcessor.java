package pl.touk.sputnik.review;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ReviewProcessor {

    void process(@NotNull Review review);

    @Nullable
    ReviewResult getReviewResult();
}
