package pl.touk.sputnik;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.xerces.impl.dv.util.Base64;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;

public abstract class AbstractConnector {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractConnector.class);
    private static int REQUEST_COUNTER = 0;
    private String host;
    private int port;
    private String username;
    private String password;
    private boolean useHttps;
    private HttpHost httpHost;
    private CredentialsProvider credentialsProvider;
    private CloseableHttpClient httpClient;
    private HttpClientContext httpClientContext;
    private DigestScheme digestScheme;
    private BasicAuthCache basicAuthCache;

    public AbstractConnector(String host, int port, String username, String password, boolean useHttps) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.useHttps = useHttps;
        createHttpContext();
    }

    @NotNull
    public abstract String listFiles(Patchset patchset) throws URISyntaxException, IOException;

    @NotNull
    public abstract String setReview(Patchset patchset, String reviewInputAsJson) throws URISyntaxException, IOException;

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
    protected CloseableHttpResponse logAndExecute(@NotNull HttpRequestBase request) throws IOException {
        LOG.info("Request  {}: {} to {}", ++REQUEST_COUNTER, request.getMethod(), request.getURI().toString());
        CloseableHttpResponse httpResponse = httpClient.execute(request, httpClientContext);
        LOG.info("Response {}: {}", REQUEST_COUNTER, httpResponse.getStatusLine().toString());
        return httpResponse;
    }

    @NotNull
    protected String consumeAndLogEntity(@NotNull CloseableHttpResponse response) throws IOException {
        if (response.getEntity() == null) {
            LOG.debug("Entity {}: no entity", REQUEST_COUNTER);
            return StringUtils.EMPTY;
        }
        String content = EntityUtils.toString(response.getEntity());
        LOG.info("Entity {}: {}", REQUEST_COUNTER, content);
        return content;
    }

    protected void addBasicAuthHeader(HttpRequest request) {
        String encoding = Base64.encode((username + ":" + password).getBytes());
        request.setHeader("Authorization", "Basic " + encoding);
    }

    protected String getHost() {
        if (useHttps) {
            return String.format("https://%s:%s", host, port);
        } else {
            return String.format("http://%s:%s", host, port);
        }
    }
}
