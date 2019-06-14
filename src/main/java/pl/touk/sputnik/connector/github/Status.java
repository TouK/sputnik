package pl.touk.sputnik.connector.github;

import com.google.common.base.Optional;
import com.jcabi.github.Commit;
import com.jcabi.github.Pull;
import com.jcabi.github.Repo;
import com.jcabi.github.Statuses;
import lombok.extern.slf4j.Slf4j;
import pl.touk.sputnik.review.Review;

import java.io.IOException;
import java.util.Iterator;

@Slf4j
class Status {
    public static final String CONTEXT = "Sputnik";
    private final Pull pull;
    private final Review review;
    private final Optional<Integer> issueId;

    public Status(Pull pull, Review review, Optional<Integer> issueId) {
        this.pull = pull;
        this.review = review;
        this.issueId = issueId;
    }

    public void update() {
        try {
            Commit commit = getLastComit(pull.commits());

            Statuses statuses = pull.repo().git().commits().statuses(commit.sha());
            statuses.create(createStatus(review, issueId));
        } catch (IOException ex) {
            log.error("Got error adding status info", ex);
        }
    }

    private Statuses.StatusCreate createStatus(Review review, Optional<Integer> issueId) {
        if (review.getFiles().size() != 0) {
            return new Statuses.StatusCreate(com.jcabi.github.Status.State.FAILURE)
                    .withContext(Optional.of(CONTEXT))
                    .withDescription("Sputnik says you have code smells in this branch")
                    .withTargetUrl(Optional.of(createIssueLink(issueId)));
        } else {
            return new Statuses.StatusCreate(com.jcabi.github.Status.State.SUCCESS)
                    .withContext(Optional.of(CONTEXT))
                    .withDescription("Looks good to me")
                    .withTargetUrl(Optional.of(createIssueLink(issueId)));
        }
    }

    private String createIssueLink(Optional<Integer> issueId) {
        final Repo repo = pull.repo();
        if (issueId.isPresent()) {
            return String.format("https://github.com/%s/%s/issues/%d",
                    repo.coordinates().user(), repo.coordinates().repo(), issueId.get());
        } else {
            return String.format("https://github.com/%s/%s/issues",
                    repo.coordinates().user(), repo.coordinates().repo());
        }
    }

    private Commit getLastComit(Iterable<Commit> commits) {
        Iterator<Commit> iterator = commits.iterator();
        Commit commit = iterator.next();
        while (iterator.hasNext()) {
            commit = iterator.next();
        }
        return commit;
    }
}
