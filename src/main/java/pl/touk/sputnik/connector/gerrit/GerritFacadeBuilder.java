package pl.touk.sputnik.connector.gerrit;

import org.apache.http.HttpHost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.ConfigurationHolder;
import pl.touk.sputnik.configuration.CliOption;
import pl.touk.sputnik.connector.ConnectorDetails;
import pl.touk.sputnik.connector.http.HttpConnector;
import pl.touk.sputnik.connector.http.HttpHelper;

import static org.apache.commons.lang3.Validate.notBlank;

public class GerritFacadeBuilder {

    private HttpHelper httpHelper = new HttpHelper();

    @NotNull
    public GerritFacade build() {
        ConnectorDetails connectorDetails = new ConnectorDetails();
        GerritPatchset gerritPatchset = buildGerritPatchset();

        HttpHost httpHost = httpHelper.buildHttpHost(connectorDetails);
        HttpClientContext httpClientContext = httpHelper.buildClientContext(httpHost, new DigestScheme());
        CloseableHttpClient closeableHttpClient = httpHelper.buildClient(httpHost, connectorDetails);

        return new GerritFacade(new GerritConnector(new HttpConnector(closeableHttpClient, httpClientContext, connectorDetails.getPath()), gerritPatchset));
    }

    @NotNull
    private GerritPatchset buildGerritPatchset() {
        String changeId = ConfigurationHolder.instance().getProperty(CliOption.CHANGE_ID);
        String revisionId = ConfigurationHolder.instance().getProperty(CliOption.REVISION_ID);

        notBlank(changeId, "You must provide non blank Gerrit change Id");
        notBlank(revisionId, "You must provide non blank Gerrit revision Id");

        return new GerritPatchset(changeId, revisionId);
    }
}
