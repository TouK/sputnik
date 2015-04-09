package pl.touk.sputnik.connector.github;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
import com.google.common.base.Optional;
import com.jcabi.github.*;
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
import java.util.Iterator;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class GithubFacade implements ConnectorFacade {

    public static final String SPUTNIK_PREFIX = "[Sputnik]";

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
        Optional<Integer> issueId = getSputnikIssue();
        if (!review.getFiles().isEmpty()) {
            if (issueId.isPresent()) {
                appendComment(issueId.get());
            } else {
                createIssue();
            }
        } else if (issueId.isPresent()) {
            commentAndClose(issueId.get());
        }

        updateStatus();
    }

    private void updateStatus() {
        try {
            Pull pull = getPull();
            String sha = getLastComitSha(pull.commits());

            Statuses statuses = pull.repo().git().commits().statuses(sha);
            statuses.create(new RtStatus(StatusState.failure, "http://some.distant.url", "Sample description", "ctx"));
        } catch (IOException ex) {
            log.error("Got error adding status info", ex);
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

    private void commentAndClose(int issueId) {
        try {
            Issue.Smart issue = new Issue.Smart(repo.issues().get(issueId));
            issue.comments().post("All's well. Great job!");
            issue.close();
        } catch (IOException e) {
            log.error("Error posting closing comment to issue #"+ issueId, e);
        }
    }

    private Optional<Integer> getSputnikIssue() {
        Optional<Integer> sputnikIssue = Optional.absent();
        for (Issue issue : repo.issues().iterate(Maps.<String, String>newHashMap())) {
            Issue.Smart smartIssue = new Issue.Smart(issue);
            try {
                if (smartIssue.title().startsWith(SPUTNIK_PREFIX)) {
                    sputnikIssue = Optional.of(smartIssue.number());
                }
            } catch (IOException e) {
                log.error("Error getting issue title for issue #" + issue.number(), e);
            }
        }
        return sputnikIssue;
    }

    private void appendComment(int issueId) {
        try {
            repo.issues().get(issueId).comments().post("Now there are more violations");
        } catch (IOException e) {
            log.error("Error adding comment to existing issue #" + issueId, e);
        }
    }

    private void createIssue() {
        try {
            Issue issue = repo.issues().create(SPUTNIK_PREFIX + " Sorry bro, there are errors", "There are some issues with your code");
            log.info("Created issue {}", issue.number());
        } catch (IOException e) {
            log.error("Error adding an issue to Github", e);
        }
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