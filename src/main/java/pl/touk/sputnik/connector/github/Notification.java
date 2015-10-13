package pl.touk.sputnik.connector.github;

import com.beust.jcommander.internal.Maps;
import com.google.common.base.Optional;
import com.jcabi.github.Issues;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
class Notification {

    public static final String SPUTNIK_PREFIX = "[Sputnik]";
    public static final String ISSUE_TITLE = " Detected some code smells";

    private final Issues issues;
    private final ContentRenderer renderer;

    public Notification(Issues issues, ContentRenderer renderer) {
        this.issues = issues;
        this.renderer = renderer;
    }

    public Optional<Integer> upsertComment(ReviewStatus reviewStatus) {
        Optional<Integer> issueId = getSputnikIssue();
        String content = reviewStatus.description(renderer);
        if (reviewStatus.isAlarming()) {
            if (issueId.isPresent()) {
                appendComment(issueId.get(),content);
            } else {
                issueId = createIssue(content);
            }
        } else if (issueId.isPresent()) {
            commentAndClose(issueId.get(), content);
        }
        return issueId;
    }

    private void commentAndClose(int issueId, String content) {
        try {
            com.jcabi.github.Issue.Smart issue = new com.jcabi.github.Issue.Smart(issues.get(issueId));
            issue.comments().post(content);
            issue.close();
        } catch (IOException e) {
            log.error("Error posting closing comment to issue #"+ issueId, e);
        }
    }

    private Optional<Integer> getSputnikIssue() {
        Optional<Integer> sputnikIssue = Optional.absent();
        for (com.jcabi.github.Issue issue : issues.iterate(Maps.<String, String>newHashMap())) {
            com.jcabi.github.Issue.Smart smartIssue = new com.jcabi.github.Issue.Smart(issue);
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

    private void appendComment(int issueId, String content) {
        try {
            issues.get(issueId).comments().post(content);
        } catch (IOException e) {
            log.error("Error adding comment to existing issue #" + issueId, e);
        }
    }

    private Optional<Integer> createIssue(String content) {
        try {
            com.jcabi.github.Issue issue = issues.create(SPUTNIK_PREFIX + ISSUE_TITLE, content);
            log.info("Created issue {}", issue.number());
            return Optional.of(issue.number());
        } catch (IOException e) {
            log.error("Error adding an issue to Github", e);
        }
        return Optional.absent();
    }
}
