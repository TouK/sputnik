package pl.touk.sputnik.connector.stash;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
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
import pl.touk.sputnik.connector.stash.json.Anchor;
import pl.touk.sputnik.connector.stash.json.FileComment;
import pl.touk.sputnik.connector.stash.json.ReviewElement;
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

            List<ReviewFile> files = new ArrayList<ReviewFile>();
            for (ReviewElement container : onlyScala(containers)) { // FIXME - not only scala, just configurable
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
                for (ReviewFileComment comment : review.getValue()) {
                    String json = objectMapper.writeValueAsString(toFileComment(review.getKey(), (ReviewLineComment) comment));
                    stashConnector.sendReview(json);
                }
            }
        } catch (URISyntaxException | IOException e) {
            throw new StashException("Error setting review", e);
        }
    }

    private FileComment toFileComment(String key, ReviewLineComment comment) {
        FileComment fileComment = new FileComment();
        fileComment.text = comment.message;
        fileComment.anchor = new Anchor();
        fileComment.anchor.path = key;
        fileComment.anchor.srcPath = key;
        fileComment.anchor.line = comment.line;
        return fileComment;
    }

    private List<ReviewElement> onlyScala(List<ReviewElement> transform) {
        return FluentIterable.from(transform)
                .filter(new Predicate<ReviewElement>() {
                    public boolean apply(ReviewElement container) {
                        return "scala".equals(container.extension);
                    }
                }).toList();
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
}
