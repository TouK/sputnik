package pl.touk.sputnik.connector;

import pl.touk.sputnik.Patchset;
import pl.touk.sputnik.connector.gerrit.json.ReviewInput;
import pl.touk.sputnik.review.ReviewFile;

import java.util.List;

public interface ConnectorFacade {
    String name();

    Patchset createPatchset();

    List<ReviewFile> listFiles(Patchset patchSet);

    void setReview(Patchset patchSet, ReviewInput reviewInput);
}
