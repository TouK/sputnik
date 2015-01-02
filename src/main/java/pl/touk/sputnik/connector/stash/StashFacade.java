package pl.touk.sputnik.connector.stash;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;

import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationHolder;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.configuration.GeneralOptionNotSupportedException;
import pl.touk.sputnik.connector.ConnectorFacade;
import pl.touk.sputnik.connector.Connectors;
import pl.touk.sputnik.connector.stash.json.*;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class StashFacade implements ConnectorFacade {
    private StashConnector stashConnector;
    private ObjectMapper objectMapper = new ObjectMapper();

    public StashFacade(@NotNull StashConnector stashConnector) {
        this.stashConnector = stashConnector;
    }

    @Override
    public Connectors name() {
        return Connectors.STASH;
    }

    @NotNull
    @Override
    public List<ReviewFile> listFiles() {
        try {
            String response = stashConnector.listFiles();
            List<JSONObject> jsonList = JsonPath.read(response, "$.values[*].path");
            List<ReviewElement> containers = transform(jsonList, ReviewElement.class);

            List<ReviewFile> files = new ArrayList<>();
            for (ReviewElement container : containers) {
                String filePath = String.format("%s/%s", container.parent, container.name);
                files.add(new ReviewFile(filePath));
            }
            return files;
        } catch (URISyntaxException | IOException e) {
            throw new StashException("Error when listing files", e);
        }
    }

    @Override
    public void setReview(@NotNull Review review) {
        sendFileComments(review);

        try {
            String json = objectMapper.writeValueAsString(new Comment(Joiner.on(". ").join(review.getMessages())));
            stashConnector.sendReview(json);
        } catch (URISyntaxException | IOException e) {
            throw new StashException("Error setting review", e);
        }
    }

    private void sendFileComments(Review review) {
        boolean commentOnlyChangedLines = Boolean.parseBoolean(ConfigurationHolder.instance().getProperty(GeneralOption.COMMENT_ONLY_CHANGED_LINES));

        for (ReviewFile reviewFile : review.getFiles()) {
            SingleFileChanges changes = changesForSingleFile(reviewFile.getReviewFilename());
            for (pl.touk.sputnik.review.Comment comment : reviewFile.getComments()) {
                CrcMessage lineComment = new CrcMessage(comment.getLine(), comment.getMessage());
                if (noCommentExists(changes, lineComment)) {
                    ChangeType changeType = getChangeType(changes, comment.getLine());
                    if (changeType.equals(ChangeType.NONE) && commentOnlyChangedLines) {
                        log.info("Not posting out of context warning: {}", comment.getMessage());
                        continue;
                    }

                    try {
                        String json = objectMapper.writeValueAsString(toFileComment(reviewFile.getReviewFilename(), comment, changeType));
                        stashConnector.sendReview(json);
                    } catch (URISyntaxException | IOException e) {
                        throw new StashException("Error setting review", e);
                    }
                }

            }
        }
    }

    private boolean noCommentExists(SingleFileChanges changes, CrcMessage lineComment) {
        return !changes.getChangesMap().containsKey(lineComment.getLine())
                || !changes.getCommentsCrcSet().contains(lineComment.getMessage());
    }

    private ChangeType getChangeType(SingleFileChanges changes, Integer line) {
        if (changes.getChangesMap().containsKey(line)) {
            return changes.getChangesMap().get(line);
        }
        return ChangeType.NONE;
    }

    private FileComment toFileComment(String key, pl.touk.sputnik.review.Comment comment, ChangeType changeType) {
        FileComment fileComment = new FileComment();
        fileComment.setText(comment.getMessage());
        Anchor anchor = Anchor.builder().
                path(key).
                srcPath(key).
                line(comment.getLine()).
                lineType(changeType.getNameForStash()).
                build();
        fileComment.setAnchor(anchor);
        return fileComment;
    }

    private <T> List<T> transform(List<JSONObject> jsonList, Class<T> someClass) {
        List<T> result = Lists.newArrayList();
        try {
            for (JSONObject jsonObject : jsonList) {
                result.add(objectMapper.readValue(jsonObject.toJSONString(), someClass));
            }
        } catch (IOException e) {
            throw new StashException("Error parsing json strings to objects", e);
        }
        return result;
    }

    SingleFileChanges changesForSingleFile(String filename) {
        try {
            String response = stashConnector.getDiffByLine(filename);
            List<JSONObject> diffJsonList = JsonPath.read(response, "$.diffs[*].hunks[*].segments[*]");
            List<String> lineList = JsonPath.read(response, "$.diffs[*].lineComments[*].text");
            List<DiffSegment> segments = transform(diffJsonList, DiffSegment.class);
            SingleFileChanges changes = SingleFileChanges.builder().filename(filename).build();
            changes.setComments(lineList);
            for (DiffSegment segment : segments) {
                for (LineSegment line : segment.lines) {
                    changes.addChange(line.destination, ChangeType.valueOf(segment.type));
                }
            }
            return changes;
        } catch (URISyntaxException | IOException e) {
            throw new StashException("Error parsing json strings to objects", e);
        }
    }

    @Override
    public void validate(Configuration configuration) throws GeneralOptionNotSupportedException {
        // all features are suppored by Stash
    }
}
