package pl.touk.sputnik.connector.stash;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.connector.ConnectorFacade;
import pl.touk.sputnik.connector.gerrit.json.ReviewFileComment;
import pl.touk.sputnik.connector.gerrit.json.ReviewInput;
import pl.touk.sputnik.connector.gerrit.json.ReviewLineComment;
import pl.touk.sputnik.connector.http.HttpConnector;
import pl.touk.sputnik.connector.stash.json.*;
import pl.touk.sputnik.review.ReviewFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import pl.touk.sputnik.Connectors;

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
            throw new StashException("Error listing files", e);
        }
    }

    @Override
    public void setReview(ReviewInput reviewInput) {
        try {
            for (Map.Entry<String, List<ReviewFileComment>> review : reviewInput.comments.entrySet()) {
                log.info("{} : {}", review.getKey(), Joiner.on(", ").join(review.getValue()));
                SingleFileChanges changes = changesForSingleFile(review.getKey());
                for (ReviewFileComment comment : review.getValue()) {
                    ReviewLineComment lineComment = (ReviewLineComment) comment;
                    if (noCommentExists(changes, lineComment.line)) {
                        String json = objectMapper.writeValueAsString(
                                toFileComment(review.getKey(), lineComment, getChangeType(changes, lineComment.line)));
                        stashConnector.sendReview(json);
                    }
                }
            }
            // Add comment with number of violations
            String json = objectMapper.writeValueAsString(new Comment(reviewInput.message));
            stashConnector.sendReview(json);
        } catch (URISyntaxException | IOException e) {
            throw new StashException("Error setting review", e);
        }
    }

    private boolean noCommentExists(SingleFileChanges changes, Integer line) {
        //if (changes.getChangesMap() != null && changes.getChangesMap().containsKey(line))
        return true;
    }

    private ChangeType getChangeType(SingleFileChanges changes, Integer line) {
        if (changes.getChangesMap() != null && changes.getChangesMap().containsKey(line)) {
            return changes.getChangesMap().get(line);
        }
        return ChangeType.CONTEXT;
    }

    private FileComment toFileComment(String key, ReviewLineComment comment, ChangeType changeType) {
        FileComment fileComment = new FileComment();
        fileComment.setText(comment.message);
        Anchor anchor = Anchor.builder().
                path(key).
                srcPath(key).
                line(comment.line).
                lineType(changeType.name()).
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
            List<JSONObject> jsonList = JsonPath.read(response, "$.diffs[*].hunks[*].segments[*]");
            List<DiffSegment> segments = transform(jsonList, DiffSegment.class);
            SingleFileChanges changes = SingleFileChanges.builder().filename(filename).build();
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
}
