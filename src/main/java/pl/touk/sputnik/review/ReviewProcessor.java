package pl.touk.sputnik.review;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ReviewProcessor {

    void process(@NotNull Review review);

    @NotNull
    String getName();

    @Nullable
    ReviewResult getReviewResult();
}
