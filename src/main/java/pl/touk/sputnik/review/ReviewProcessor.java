package pl.touk.sputnik.review;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ReviewProcessor {

    @Nullable
    ReviewResult process(@NotNull Review review);

    @NotNull
    String getName();
}
