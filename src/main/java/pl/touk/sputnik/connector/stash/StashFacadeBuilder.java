package pl.touk.sputnik.connector.stash;

import org.apache.http.HttpHost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.CliOption;
import pl.touk.sputnik.configuration.ConfigurationHolder;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.connector.ConnectorDetails;
import pl.touk.sputnik.connector.http.HttpHelper;

import static org.apache.commons.lang3.Validate.notBlank;

public class StashFacadeBuilder {

    private HttpHelper httpHelper = new HttpHelper();

    @NotNull
    public StashFacade build() {
        ConnectorDetails connectorDetails = new ConnectorDetails().build();
        StashPatchset stashPatchset = buildStashPatchset();

        HttpHost httpHost = httpHelper.buildHttpHost(connectorDetails);
        HttpClientContext httpClientContext = httpHelper.buildClientContext(httpHost, new BasicScheme());
        CloseableHttpClient closeableHttpClient = httpHelper.buildClient(httpHost, connectorDetails);

        return new StashFacade(closeableHttpClient, httpClientContext, stashPatchset);
    }

    @NotNull
    public StashPatchset buildStashPatchset() {
        String pullRequestId = ConfigurationHolder.instance().getProperty(CliOption.PULL_REQUEST_ID);
        String repositorySlug = ConfigurationHolder.instance().getProperty(GeneralOption.REPOSITORY_SLUG);
        String projectKey = ConfigurationHolder.instance().getProperty(GeneralOption.PROJECT_KEY);

        notBlank(pullRequestId, "You must provide non blank Stash pull request id");
        notBlank(repositorySlug, "You must provide non blank Stash repository slug");
        notBlank(projectKey, "You must provide non blank Stash project key");

        return new StashPatchset(pullRequestId, repositorySlug, projectKey);
    }
}
