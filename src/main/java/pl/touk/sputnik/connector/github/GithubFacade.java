package pl.touk.sputnik.connector.github;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.jcabi.github.Commit;
import com.jcabi.github.Pull;
import com.jcabi.github.Repo;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOptionNotSupportedException;
import pl.touk.sputnik.connector.ConnectorFacade;
import pl.touk.sputnik.connector.Connectors;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;

import javax.annotation.Nullable;
import javax.json.JsonObject;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class GithubFacade implements ConnectorFacade {

    private final Repo repo;

    private final GithubPatchset githubPatchset;

    private CommentableLineFilter lineFilter = new CommentableLineFilter(new ExtractDiffFromCommits(), new DiffParser());

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


    // for each remaining file run git blame to find out which commit is responsible for that change
    //  -- check if the line with warning was modified by a commit from `commitedShas`
    //  -- do a diff as in GithubFacadeTest
    //  -- parse diff
    //  -- add comments for remaining review items
    @Override
    public void setReview(@NotNull Review review) {
        final Pull pull = getPull();

        final List<String> commitedShas = shaCommits(pull);
        final Map<String, Map<Integer, Pair<Integer, String>>> linesMapping = Maps.newHashMap();

        // for each file, check if current branch changes those files at all
        for (ReviewFile reviewFile : review.getFiles()) {
            String filename = reviewFile.getReviewFilename();
            // check if the line with warning was modified by a commit from `commitedShas`
            linesMapping.put(filename, lineFilter.commentableLines(commitedShas, reviewFile, "HEAD"));
        }

        for (ReviewFile reviewFile : Iterables.filter(review.getFiles(), new Predicate<ReviewFile>() {
            @Override
            public boolean apply(@Nullable ReviewFile input) {
                assert input != null;
                return linesMapping.containsKey(input.getReviewFilename());
            }
        })) {
            String filename = reviewFile.getReviewFilename();
            Map<Integer, Pair<Integer, String>> linesInFile = linesMapping.get(filename);
            log.info("{}: with keys {}", filename, Joiner.on(",").withKeyValueSeparator(":").join(linesInFile));
            for (pl.touk.sputnik.review.Comment comment : reviewFile.getComments()) {
                if (linesInFile.containsKey(comment.getLine())) {
                    String sha = linesInFile.get(comment.getLine()).getRight();
                    Integer relativeLineNo = linesInFile.get(comment.getLine()).getLeft();
                    log.info("{}:{} - {}", filename, relativeLineNo, comment.getMessage());
                    postComment(pull, relativeLineNo, comment.getMessage(), sha, filename);
                }
            }
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

    private List<String> shaCommits(Pull pull) {
        List<String> commitedShas = Lists.newArrayList();
        try {
            for (Commit commit : pull.commits()) {
                commitedShas.add(commit.sha());
            }
        } catch (IOException e) {
            log.error("Error getting SHA for commits", e);
        }
        return commitedShas;
    }

    private List<String> files(Pull pull) {
        List<String> result = Lists.newArrayList();
        try {
            for (JsonObject json : pull.files()) {
                result.add(json.getString("filename"));
            }
        } catch (IOException e) {
            log.error("Error getting file list from PullRequest", e);
        }
        return result;
    }
}
