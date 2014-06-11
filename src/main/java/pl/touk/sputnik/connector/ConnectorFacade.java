package pl.touk.sputnik.connector;

import pl.touk.sputnik.connector.gerrit.json.ReviewInput;
import pl.touk.sputnik.review.ReviewFile;

import java.util.List;

public interface ConnectorFacade {
    String name();

    List<ReviewFile> listFiles();

    void setReview(ReviewInput reviewInput);
}
