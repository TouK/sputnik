package pl.touk.sputnik.connector.saas;

import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.CliOption;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.connector.ConnectorDetails;
import pl.touk.sputnik.connector.github.GithubPatchset;
import pl.touk.sputnik.connector.github.GithubPatchsetBuilder;
import pl.touk.sputnik.connector.http.HttpConnector;
import pl.touk.sputnik.connector.http.HttpHelper;

public class SaasFacadeBuilder {

    private HttpHelper httpHelper = new HttpHelper();

    @NotNull
    public SaasFacade build(Configuration configuration) {
        ConnectorDetails connectorDetails = new ConnectorDetails(configuration);
        String apiKey = configuration.getProperty(CliOption.API_KEY);
        HttpHost httpHost = httpHelper.buildHttpHost(connectorDetails);
        HttpClientContext httpClientContext = httpHelper.buildClientContext(httpHost, new BasicScheme());
        CloseableHttpClient closeableHttpClient = httpHelper.buildClient(httpHost, connectorDetails);

        HttpConnector httpConnector = new HttpConnector(closeableHttpClient, httpClientContext, connectorDetails.getPath());
        GithubPatchset patchset = GithubPatchsetBuilder.build(configuration);

        SaasConnector saasConnector = StringUtils.isNotBlank(apiKey) ?
                new SaasConnector(httpConnector, patchset, apiKey) :
                new SaasConnector(httpConnector, patchset);

        return new SaasFacade(saasConnector, new Gson());
    }

}
