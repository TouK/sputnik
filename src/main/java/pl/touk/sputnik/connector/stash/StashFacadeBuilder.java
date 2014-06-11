package pl.touk.sputnik.connector.stash;

import org.apache.http.HttpHost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.CliOption;
import pl.touk.sputnik.connector.http.HttpHelper;

import static org.apache.commons.lang3.Validate.notBlank;

public class StashFacadeBuilder {

    private HttpHelper httpHelper = new HttpHelper();

    public StashFacade build() {
        String host = Configuration.instance().getProperty(StashFacade.STASH_HOST);
        String port = Configuration.instance().getProperty(StashFacade.STASH_PORT);
        String username = Configuration.instance().getProperty(StashFacade.STASH_USERNAME);
        String password = Configuration.instance().getProperty(StashFacade.STASH_PASSWORD);
        String useHttps = Configuration.instance().getProperty(StashFacade.STASH_USE_HTTPS);

        notBlank(host, "You must provide non blank Stash host");
        notBlank(port, "You must provide non blank Stash port");
        notBlank(username, "You must provide non blank Stash username");
        notBlank(password, "You must provide non blank Stash password");

        StashPatchset stashPatchset = buildStashPatchset();

        HttpHost httpHost = httpHelper.buildHttpHost(host, Integer.valueOf(port), Boolean.parseBoolean(useHttps));
        HttpClientContext httpClientContext = httpHelper.buildClientContext(httpHost);
        CloseableHttpClient closeableHttpClient = httpHelper.buildClient(httpHost, username, password);

        return new StashFacade(closeableHttpClient, httpClientContext, stashPatchset);
    }

    public StashPatchset buildStashPatchset() {
        String pullRequestId = Configuration.instance().getProperty(CliOption.PULL_REQUEST_ID);
        String repositorySlug = Configuration.instance().getProperty(StashFacade.STASH_REPOSITORY_SLUG);
        String projectKey = Configuration.instance().getProperty(StashFacade.STASH_PROJECT_KEY);

        notBlank(pullRequestId, "You must provide non blank Stash pull request id");
        notBlank(repositorySlug, "You must provide non blank Stash repository slug");
        notBlank(projectKey, "You must provide non blank Stash project key");
        return new StashPatchset(pullRequestId, repositorySlug, projectKey);
    }
}
