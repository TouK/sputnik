package pl.touk.sputnik.connector.saas;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOptionNotSupportedException;
import pl.touk.sputnik.connector.ConnectorFacade;
import pl.touk.sputnik.connector.Connectors;
import pl.touk.sputnik.connector.http.HttpException;
import pl.touk.sputnik.connector.saas.json.FileViolation;
import pl.touk.sputnik.connector.saas.json.Violation;
import pl.touk.sputnik.review.Comment;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class SaasFacade implements ConnectorFacade {

    private SaasConnector saasConnector;
    private Gson gson;

    @Override
    public Connectors name() {
        return Connectors.SAAS;
    }

    @NotNull
    @Override
    public List<ReviewFile> listFiles() {
        try {
            String response = saasConnector.listFiles();
            String[] files = gson.fromJson(response, String[].class);
            List<ReviewFile> reviewFiles = new ArrayList<>();
            for (String filename : files) {
                reviewFiles.add(new ReviewFile(filename));
            }
            return reviewFiles;
        } catch (HttpException ex) {
            throw new SaasException("Error when listing files, check your api key", ex);
        } catch (URISyntaxException | IOException ex) {
            throw new SaasException("Error when listing files", ex);
        }
    }

    @Override
    public void publish(Review review) {
        List<FileViolation> fileViolations = new ArrayList<>();
        for (ReviewFile reviewFile : review.getFiles()) {
            List<Violation> violations = new ArrayList<>();
            for (Comment comment : reviewFile.getComments()) {
                violations.add(new Violation(comment.getLine(), comment.getMessage()));
            }
            fileViolations.add(new FileViolation(reviewFile.getReviewFilename(), violations));
        }
        String request = gson.toJson(fileViolations);
        try {
            saasConnector.sendReview(request);
        } catch (HttpException ex) {
            throw new SaasException("Error when listing files, check your api key", ex);
        } catch (URISyntaxException | IOException ex) {
            throw new SaasException("Error while publishing review", ex);
        }
    }

    @Override
    public void validate(Configuration configuration) throws GeneralOptionNotSupportedException {
    }

    @Override
    public void setReview(@NotNull Review review) {
        publish(review);
    }

}
