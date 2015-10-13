package pl.touk.sputnik.connector.saas;

import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOptionNotSupportedException;
import pl.touk.sputnik.connector.ConnectorFacade;
import pl.touk.sputnik.connector.Connectors;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;

import java.util.List;

public class SaasFacade implements ConnectorFacade {

	@Override
	public Connectors name() {
		return Connectors.SAAS;
	}

	@NotNull
	@Override
	public List<ReviewFile> listFiles() {
		return null;
	}

	@Override
	public void validate(Configuration configuration) throws GeneralOptionNotSupportedException {

	}

	@Override
	public void setReview(@NotNull Review review) {
        publish(review);
	}

	@Override
	public void publish(Review review) {

	}

}
