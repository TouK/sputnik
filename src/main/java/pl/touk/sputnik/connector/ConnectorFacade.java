package pl.touk.sputnik.connector;

import pl.touk.sputnik.connector.gerrit.json.ReviewInput;
import pl.touk.sputnik.review.ReviewFile;

import java.util.List;
import pl.touk.sputnik.Connectors;

public interface ConnectorFacade {
    Connectors name();

    List<ReviewFile> listFiles();

    void setReview(ReviewInput reviewInput);
}
