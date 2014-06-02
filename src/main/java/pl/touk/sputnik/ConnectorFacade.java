package pl.touk.sputnik;

import pl.touk.sputnik.gerrit.json.ReviewInput;
import pl.touk.sputnik.review.ReviewFile;

import java.util.List;

public interface ConnectorFacade {
    String name();

    Patchset createPatchset();

    List<ReviewFile> listFiles(Patchset patchSet);

    void setReview(Patchset patchSet, ReviewInput reviewInput);
}
