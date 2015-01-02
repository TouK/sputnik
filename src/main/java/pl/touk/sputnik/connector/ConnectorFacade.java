package pl.touk.sputnik.connector;

import org.jetbrains.annotations.NotNull;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOptionNotSupportedException;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;

import java.util.List;

public interface ConnectorFacade {
    Connectors name();

    @NotNull
    List<ReviewFile> listFiles();

    /**
     * Validates if given options are supported by selected connector.
     * 
     * @throws GeneralOptionNotSupportedException
     *             if passed configuration is not valid or not fully supported
     */
    void validate(Configuration configuration) throws GeneralOptionNotSupportedException;

    void setReview(@NotNull Review review);
}
