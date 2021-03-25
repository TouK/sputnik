package pl.touk.sputnik.connector.gerrit;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.gerrit.extensions.api.GerritApi;
import com.google.gerrit.extensions.restapi.Url;
import com.urswolfer.gerrit.client.rest.GerritAuthData;
import com.urswolfer.gerrit.client.rest.GerritRestApiFactory;
import com.urswolfer.gerrit.client.rest.http.HttpClientBuilderExtension;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.CliOption;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.connector.ConnectorDetails;
import pl.touk.sputnik.connector.http.HttpHelper;

import static org.apache.commons.lang3.Validate.notBlank;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
public class GerritFacadeBuilder {

    private final HttpHelper httpHelper = new HttpHelper();

    @NotNull
    public GerritFacade build(Configuration configuration) {
        final ConnectorDetails connectorDetails = new ConnectorDetails(configuration);
        GerritPatchset gerritPatchset = buildGerritPatchset(configuration);

        GerritRestApiFactory gerritRestApiFactory = new GerritRestApiFactory();
        String hostUri = httpHelper.buildHttpHostUri(connectorDetails);
        if (!Strings.isNullOrEmpty(connectorDetails.getPath())) {
            hostUri += connectorDetails.getPath();
        }

        GerritOptions gerritOptions = GerritOptions.from(configuration);

        log.info("Using Gerrit URL: {}", hostUri);
        GerritAuthData.Basic authData = new GerritAuthData.Basic(hostUri,
                connectorDetails.getUsername(), connectorDetails.getPassword(),
                gerritOptions.isUseHttpPassword());
        GerritApi gerritApi = gerritRestApiFactory.create(authData, new HttpClientBuilderExtension() {
            @Override
            public HttpClientBuilder extend(HttpClientBuilder httpClientBuilder, GerritAuthData authData) {
                HttpClientBuilder clientBuilder = super.extend(httpClientBuilder, authData);
                clientBuilder.setSSLSocketFactory(httpHelper.buildSSLSocketFactory(connectorDetails));
                return clientBuilder;
            }
        });

        return new GerritFacade(gerritApi, gerritPatchset, gerritOptions);
    }

    @NotNull
    private GerritPatchset buildGerritPatchset(Configuration configuration) {
        String changeId = configuration.getProperty(CliOption.CHANGE_ID);
        String revisionId = configuration.getProperty(CliOption.REVISION_ID);
        String tag = configuration.getProperty(GeneralOption.TAG);

        notBlank(changeId, "You must provide non blank Gerrit change Id");
        notBlank(revisionId, "You must provide non blank Gerrit revision Id");

        return new GerritPatchset(urlEncodeChangeId(changeId), revisionId, tag);
    }

    public static String urlEncodeChangeId(String changeId) {
        if (changeId.indexOf('%') >= 0) {
            // ChangeID is already encoded (otherwise why it would have a '%' character?)
            return changeId;
        }
        // To keep the changeId readable, we don't encode '~' (it is not needed according to RFC2396)
        // See also: ChangesRestClient.id(String, String) and ChangesRestClient.id(String, String, String)
        return StreamSupport.stream(Splitter.on('~').split(changeId).spliterator(), false)
                .map(Url::encode).collect(Collectors.joining("~"));
    }
}
