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
     * Validates if given options are supported by selected connector. If not it throws an exception with information
     * which option won't be supported and shall be changed.
     * 
     * @throws GeneralOptionNotSupportedException
     *             if configuration is invalid
     */
    void supports(Configuration configuration) throws GeneralOptionNotSupportedException;

    void setReview(@NotNull Review review);
}
