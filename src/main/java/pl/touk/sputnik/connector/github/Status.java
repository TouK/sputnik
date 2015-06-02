package pl.touk.sputnik.connector.github;

import com.google.common.base.Optional;
import com.jcabi.github.*;
import lombok.extern.slf4j.Slf4j;
import pl.touk.sputnik.review.Review;

import java.io.IOException;
import java.util.Iterator;

@Slf4j
public class Status {
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
            String sha = getLastComitSha(pull.commits());

            Statuses statuses = pull.repo().git().commits().statuses(sha);
            statuses.create(createStatus(review, issueId));
        } catch (IOException ex) {
            log.error("Got error adding status info", ex);
        }
    }

    private RtStatus createStatus(Review review, Optional<Integer> issueId) {
        if (review.getFiles().size() != 0) {
            return new RtStatus(com.jcabi.github.Status.State.Failure, createIssueLink(issueId),
                    "Sputnik says you have code smells in this branch", CONTEXT);
        } else {
            return new RtStatus(com.jcabi.github.Status.State.Success, createIssueLink(issueId),
                    "Looks good to me", CONTEXT);
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

    private String getLastComitSha(Iterable<Commit> commits) {
        Iterator<Commit> iterator = commits.iterator();
        Commit commit = iterator.next();
        while (iterator.hasNext()) {
            commit = iterator.next();
        }
        return commit.sha();
    }
}
