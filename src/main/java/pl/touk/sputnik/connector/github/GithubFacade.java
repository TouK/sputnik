package pl.touk.sputnik.connector.github;

import com.beust.jcommander.internal.Lists;
import com.google.common.base.Optional;
import com.jcabi.github.Pull;
import com.jcabi.github.Repo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOptionNotSupportedException;
import pl.touk.sputnik.connector.ConnectorFacade;
import pl.touk.sputnik.connector.Connectors;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;

import javax.json.JsonObject;
import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class GithubFacade implements ConnectorFacade {



    private final Repo repo;

    private final GithubPatchset githubPatchset;

    @Override
    public Connectors name() {
        return Connectors.GITHUB;
    }

    @NotNull
    @Override
    public List<ReviewFile> listFiles() {
        Pull pull = getPull();

        List<ReviewFile> files = Lists.newArrayList();
        try {
            for (JsonObject o : pull.files()) {
                files.add(new ReviewFile(o.getString("filename")));
            }
        } catch (IOException ex) {
            log.error("Error fetching files for pull request", ex);
        }
        return files;
    }

    @Override
    public void validate(Configuration configuration) throws GeneralOptionNotSupportedException {
        // all features are supported
    }

    @Override
    public void setReview(@NotNull Review review) {
        ReviewStatus reviewStatus = new ReviewStatus(review);
        Optional<Integer> issueId = new Notification(repo.issues()).upsertComment(reviewStatus);
        new Status(getPull(), review, issueId).update();
    }

    /**
     * post comment for github pull request
     */
    private void postComment(Pull pull, int lineNo, String comment, String commitSha, String filename) {
        try {
            pull.comments().post(comment,
                    commitSha,
                    filename, lineNo);
        } catch (IOException e) {
            log.error("Error adding comment to file " + filename, e);
        }
    }

    private Pull getPull() {
        return repo.pulls().get(githubPatchset.pullRequestId);
    }


}