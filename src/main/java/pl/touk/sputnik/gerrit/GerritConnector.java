package pl.touk.sputnik.gerrit;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class GerritConnector {
    private static final Logger LOG = LoggerFactory.getLogger(GerritConnector.class);
    private static final String GET_LIST_FILES_URL_FORMAT = "/a/changes/%s/revisions/%s/files/";
    private static final String POST_SET_REVIEW_URL_FORMAT = "/a/changes/%s/revisions/%s/review";
    private static int REQUEST_COUNTER = 0;
    private String host;
    private int port;
    private String username;
    private String password;
    private HttpHost httpHost;
    private CredentialsProvider credentialsProvider;
    private CloseableHttpClient httpClient;
    private HttpClientContext httpClientContext;
    private DigestScheme digestScheme;
    private BasicAuthCache basicAuthCache;

    public GerritConnector(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        createHttpContext();
    }

    @NotNull
    public String listFiles(String changeId, String revisionId) throws URISyntaxException, IOException {
        URI uri = new URIBuilder().setPath(String.format(GET_LIST_FILES_URL_FORMAT, changeId, revisionId)).build();
        HttpGet httpGet = new HttpGet(uri);
        CloseableHttpResponse httpResponse = logAndExecute(httpGet);
        return consumeAndLogEntity(httpResponse);
    }

    @NotNull
    public String setReview(String changeId, String revisionId, String reviewInputAsJson) throws URISyntaxException, IOException {
        LOG.info("Setting review {}", reviewInputAsJson);
        URI uri = new URIBuilder().setPath(String.format(POST_SET_REVIEW_URL_FORMAT, changeId, revisionId)).build();
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setEntity(new StringEntity(reviewInputAsJson, ContentType.APPLICATION_JSON));
        CloseableHttpResponse httpResponse = logAndExecute(httpPost);
        return consumeAndLogEntity(httpResponse);
    }

    // Example http://hc.apache.org/httpcomponents-client-ga/httpclient/examples/org/apache/http/examples/client/ClientPreemptiveDigestAuthentication.java
    private void createHttpContext() {
        httpHost = new HttpHost(host, port);
        credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
            new AuthScope(host, port),
            new UsernamePasswordCredentials(username, password));
        httpClient = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();

        basicAuthCache = new BasicAuthCache();
        digestScheme = new DigestScheme();
        basicAuthCache.put(httpHost, digestScheme);
        httpClientContext = HttpClientContext.create();
        httpClientContext.setAuthCache(basicAuthCache);
    }

    @NotNull
    private CloseableHttpResponse logAndExecute(@NotNull HttpRequestBase request) throws IOException {
        LOG.info("Request  {}: {} to {}", ++REQUEST_COUNTER, request.getMethod(), request.getURI().toString());
        CloseableHttpResponse httpResponse = httpClient.execute(httpHost, request, httpClientContext);
        LOG.info("Response {}: {}", REQUEST_COUNTER, httpResponse.getStatusLine().toString());
        return httpResponse;
    }

    @NotNull
    private String consumeAndLogEntity(@NotNull CloseableHttpResponse response) throws IOException {
        if (response.getEntity() == null) {
            LOG.debug("Entity {}: no entity", REQUEST_COUNTER);
            return StringUtils.EMPTY;
        }
        String content = EntityUtils.toString(response.getEntity());
        LOG.info("Entity {}: {}", REQUEST_COUNTER, content);
        return content;
    }


}
