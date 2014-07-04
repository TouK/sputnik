package pl.touk.sputnik.connector;

import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;

import java.util.List;

public interface ConnectorFacade {
    Connectors name();

    @NotNull
    List<ReviewFile> listFiles();

    void setReview(@NotNull Review review);
}
