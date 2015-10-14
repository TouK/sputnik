package pl.touk.sputnik.connector.stash;

import org.apache.http.HttpHost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.CliOption;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.connector.ConnectorDetails;
import pl.touk.sputnik.connector.http.HttpConnector;
import pl.touk.sputnik.connector.http.HttpHelper;

import static org.apache.commons.lang3.Validate.notBlank;

public class StashFacadeBuilder {

    private HttpHelper httpHelper = new HttpHelper();

    @NotNull
    public StashFacade build(Configuration configuration) {
        ConnectorDetails connectorDetails = new ConnectorDetails(configuration);
        StashPatchset stashPatchset = buildStashPatchset(configuration);

        HttpHost httpHost = httpHelper.buildHttpHost(connectorDetails);
        HttpClientContext httpClientContext = httpHelper.buildClientContext(httpHost, new BasicScheme());
        CloseableHttpClient closeableHttpClient = httpHelper.buildClient(httpHost, connectorDetails);

        return new StashFacade(new StashConnector(
                new HttpConnector(closeableHttpClient, httpClientContext, connectorDetails.getPath()), stashPatchset), configuration);
    }

    @NotNull
    public StashPatchset buildStashPatchset(Configuration configuration) {
        String pullRequestId = configuration.getProperty(CliOption.PULL_REQUEST_ID);
        String repositorySlug = configuration.getProperty(GeneralOption.REPOSITORY);
        String projectKey = configuration.getProperty(GeneralOption.PROJECT);

        notBlank(pullRequestId, "You must provide non blank Stash pull request id");
        notBlank(repositorySlug, "You must provide non blank Stash repository slug");
        notBlank(projectKey, "You must provide non blank Stash project key");

        return new StashPatchset(pullRequestId, repositorySlug, projectKey);
    }
}
