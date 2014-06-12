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

@Slf4j
public class StashFacade implements ConnectorFacade {

    private static final String CONNECTOR_NAME = "stash";
    public static final String STASH_HOST = "stash.host";
    public static final String STASH_PORT = "stash.port";
    public static final String STASH_USE_HTTPS = "stash.useHttps";
    public static final String STASH_USERNAME = "stash.username";
    public static final String STASH_PASSWORD = "stash.password";
    public static final String STASH_PROJECT_KEY = "stash.projectKey";
    public static final String STASH_REPOSITORY_SLUG = "stash.repositorySlug";

    private StashConnector stashConnector;
    private ObjectMapper objectMapper = new ObjectMapper();

    public StashFacade(@NotNull CloseableHttpClient httpClient, @NotNull HttpClientContext httpClientContext, @NotNull StashPatchset stashPatchset) {
        stashConnector = new StashConnector(new HttpConnector(httpClient, httpClientContext), stashPatchset);
    }

    @Override
    public String name() {
        return CONNECTOR_NAME;
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
                    String json = objectMapper.writeValueAsString(
                            toFileComment(review.getKey(), lineComment, getChangeType(changes, lineComment.line)));
                    stashConnector.sendReview(json);
                }
            }
        } catch (URISyntaxException | IOException e) {
            throw new StashException("Error setting review", e);
        }
    }

    private ChangeType getChangeType(SingleFileChanges changes, Integer line) {
        if (changes.getChangesMap().containsKey(line)) {
            return changes.getChangesMap().get(line);
        }
        return ChangeType.CONTEXT;
    }

    private FileComment toFileComment(String key, ReviewLineComment comment, ChangeType changeType) {
        FileComment fileComment = new FileComment();
        fileComment.text = comment.message;
        fileComment.anchor = new Anchor();
        fileComment.anchor.path = key;
        fileComment.anchor.srcPath = key;
        fileComment.anchor.line = comment.line;
        fileComment.anchor.lineType = changeType.name();
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
