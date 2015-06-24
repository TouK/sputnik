package pl.touk.sputnik.connector.gerrit;

import com.google.common.base.Strings;
import com.google.gerrit.extensions.api.GerritApi;
import com.urswolfer.gerrit.client.rest.GerritAuthData;
import com.urswolfer.gerrit.client.rest.GerritRestApiFactory;
import com.urswolfer.gerrit.client.rest.http.HttpClientBuilderExtension;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.CliOption;
import pl.touk.sputnik.connector.ConnectorDetails;
import pl.touk.sputnik.connector.http.HttpHelper;

import static org.apache.commons.lang3.Validate.notBlank;

@Slf4j
public class GerritFacadeBuilder {

    private HttpHelper httpHelper = new HttpHelper();

    @NotNull
    public GerritFacade build(Configuration configuration) {
        ConnectorDetails connectorDetails = new ConnectorDetails(configuration);
        GerritPatchset gerritPatchset = buildGerritPatchset(configuration);

        GerritRestApiFactory gerritRestApiFactory = new GerritRestApiFactory();
        String hostUri = httpHelper.buildHttpHostUri(connectorDetails);
        if (!Strings.isNullOrEmpty(connectorDetails.getPath())) {
            hostUri += connectorDetails.getPath();
        }
        log.info("Using Gerrit URL: {}", hostUri);
        GerritAuthData.Basic authData = new GerritAuthData.Basic(hostUri,
                connectorDetails.getUsername(), connectorDetails.getPassword());
        GerritApi gerritApi = gerritRestApiFactory.create(authData, new HttpClientBuilderExtension() {
            @Override
            public HttpClientBuilder extend(HttpClientBuilder httpClientBuilder, GerritAuthData authData) {
                HttpClientBuilder clientBuilder = super.extend(httpClientBuilder, authData);
                clientBuilder.setSSLSocketFactory(httpHelper.buildSSLSocketFactory());
                return clientBuilder;
            }
        });

        return new GerritFacade(gerritApi, gerritPatchset);
    }

    @NotNull
    private GerritPatchset buildGerritPatchset(Configuration configuration) {
        String changeId = configuration.getProperty(CliOption.CHANGE_ID);
        String revisionId = configuration.getProperty(CliOption.REVISION_ID);

        notBlank(changeId, "You must provide non blank Gerrit change Id");
        notBlank(revisionId, "You must provide non blank Gerrit revision Id");

        return new GerritPatchset(changeId, revisionId);
    }
}
