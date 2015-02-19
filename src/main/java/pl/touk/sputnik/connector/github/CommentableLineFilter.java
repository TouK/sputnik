package pl.touk.sputnik.connector.github;

import com.beust.jcommander.internal.Maps;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jgit.api.errors.GitAPIException;
import pl.touk.sputnik.review.ReviewFile;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class CommentableLineFilter {

    private ExtractDiffFromCommits diffExtractor;
    private DiffParser diffParser;

    public Map<Integer, Pair<Integer, String>> commentableLines(final List<String> commitedShas, ReviewFile reviewFile, String refId) {
        String filename = reviewFile.getReviewFilename();
        Map<Integer, Pair<Integer, String>> result = Maps.newHashMap();

        try {
            Map<String, List<Integer>> blameMap = new GitBlameCommand().gitBlame(filename, refId);
            for (String sha : Iterables.filter(blameMap.keySet(), new Predicate<String>() {

                @Override
                public boolean apply(@Nullable String sha) {
                    return commitedShas.contains(sha);
                }
            })) {
                String diffContent = diffExtractor.extract(refId, sha);
                Map<String, Map<Integer, Integer>> diffAllFiles = diffParser.parse(diffContent);
                if (diffAllFiles.containsKey(filename)) {
                    Map<Integer, Integer> diff = diffAllFiles.get(filename);
                    log.info("{}:{}, i got keys = {}", filename, sha, diff.keySet());
                    for (Integer lineNo : blameMap.get(sha)) {
                        if (diff.containsKey(lineNo)) {
                            result.put(lineNo, Pair.of(diff.get(lineNo), sha));
                        }
                    }
                }
            }
        } catch (IOException | GitAPIException e) {
            log.error("Error doing git-blame for file {} and sha {}", filename, refId);
        }

        return result;
    }
}
