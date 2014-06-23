package pl.touk.sputnik.connector.gerrit;

import org.apache.http.HttpHost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import pl.touk.sputnik.configuration.ConfigurationHolder;
import pl.touk.sputnik.configuration.CliOption;
import pl.touk.sputnik.connector.http.HttpConnector;
import pl.touk.sputnik.connector.http.HttpHelper;

import static org.apache.commons.lang3.Validate.notBlank;

public class GerritFacadeBuilder {

    private HttpHelper httpHelper = new HttpHelper();

    public GerritFacade build() {
        String host = ConfigurationHolder.instance().getProperty(GerritOption.HOST);
        String port = ConfigurationHolder.instance().getProperty(GerritOption.PORT);
        String username = ConfigurationHolder.instance().getProperty(GerritOption.USERNAME);
        String password = ConfigurationHolder.instance().getProperty(GerritOption.PASSWORD);
        String useHttps = ConfigurationHolder.instance().getProperty(GerritOption.USE_HTTPS);
        boolean isHttps = Boolean.parseBoolean(useHttps);

        notBlank(host, "You must provide non blank Gerrit host");
        notBlank(port, "You must provide non blank Gerrit port");
        notBlank(username, "You must provide non blank Gerrit username");
        notBlank(password, "You must provide non blank Gerrit password");

        GerritPatchset gerritPatchset = buildGerritPatchset();

        HttpHost httpHost = httpHelper.buildHttpHost(host, Integer.valueOf(port), Boolean.parseBoolean(useHttps));
        HttpClientContext httpClientContext = httpHelper.buildClientContext(httpHost, new DigestScheme());
        CloseableHttpClient closeableHttpClient = httpHelper.buildClient(httpHost, username, password, isHttps);

        return new GerritFacade(new GerritConnector(new HttpConnector(closeableHttpClient, httpClientContext), gerritPatchset));
    }

    private GerritPatchset buildGerritPatchset() {
        String changeId = ConfigurationHolder.instance().getProperty(CliOption.CHANGE_ID);
        String revisionId = ConfigurationHolder.instance().getProperty(CliOption.REVISION_ID);

        notBlank(changeId, "You must provide non blank Gerrit change Id");
        notBlank(revisionId, "You must provide non blank Gerrit revision Id");

        return new GerritPatchset(changeId, revisionId);
    }
}
