package pl.touk.sputnik.stash;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.Configuration;
import pl.touk.sputnik.ConnectorFacade;
import pl.touk.sputnik.Patchset;
import pl.touk.sputnik.gerrit.GerritException;
import pl.touk.sputnik.gerrit.json.ListFilesResponse;
import pl.touk.sputnik.gerrit.json.ReviewInput;
import pl.touk.sputnik.review.ReviewFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.Validate.notBlank;

public class StashFacade implements ConnectorFacade {

    private static final String CONNECTOR_NAME = "stash";
    public static final String STASH_HOST = "stash.host";
    public static final String STASH_PORT = "stash.port";
    public static final String STASH_USERNAME = "stash.username";
    public static final String STASH_PASSWORD = "stash.password";
    public static final String STASH_PROJECT_KEY = "stash.projectKey";
    public static final String STASH_REPOSITORY_SLUG = "stash.repositorySlug";

    private StashConnector stashConnector;
    private ObjectMapper objectMapper = new ObjectMapper();

    public StashFacade(@NotNull String host, int port, @NotNull String username, @NotNull String password) {
        stashConnector = new StashConnector(host, port, username, password);
    }

    @Override
    public String name() {
        return CONNECTOR_NAME;
    }

    @Override
    public Patchset createPatchset() {
        String pullRequestId = Configuration.instance().getStashPullRequestId();
        String repositorySlug = Configuration.instance().getProperty(STASH_REPOSITORY_SLUG);
        String projectKey = Configuration.instance().getProperty(STASH_PROJECT_KEY);

        notBlank(pullRequestId, "You must provide non blank Stash pull request id");
        notBlank(repositorySlug, "You must provide non blank Stash repository slug");
        notBlank(projectKey, "You must provide non blank Stash project key");
        return new StashPatchset(pullRequestId, repositorySlug, projectKey);
    }

    @Override
    public List<ReviewFile> listFiles(Patchset patchset) {
        try {
            stashConnector.listFiles(patchset);
            //String jsonString = trimResponse(response);
            String jsonString = ""; // FIXME
            ListFilesResponse listFilesResponse = objectMapper.readValue(jsonString, ListFilesResponse.class);

            List<ReviewFile> files = new ArrayList<ReviewFile>();
            Set<String> keys = listFilesResponse.keySet();
            for (String key : keys) {
                files.add(new ReviewFile(key));
            }
            return files;
        } catch (URISyntaxException e) {
            throw new StashException("Error listing files", e);
        } catch (IOException e) {
            throw new StashException("Error listing files", e);
        }
    }

    @Override
    public void setReview(Patchset patchset, ReviewInput reviewInput) {
        StashPatchset stashPatchset = (StashPatchset) patchset;

    }
}
