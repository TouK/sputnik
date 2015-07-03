package pl.touk.sputnik.review;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.touk.sputnik.configuration.Configuration;

public interface ReviewProcessor {

    @Nullable
    ReviewResult process(@NotNull Review review);

    @NotNull
    String getName();
}
