package pl.touk.sputnik.review;

import org.jetbrains.annotations.NotNull;

public interface ReviewProcessor {

    void process(@NotNull Review review);
}
