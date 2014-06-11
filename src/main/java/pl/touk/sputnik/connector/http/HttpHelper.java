package pl.touk.sputnik.connector.http;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class HttpHelper {
    private static final String HTTP_SCHEME = "http";
    private static final String HTTPS_SCHEME = "https";

    public HttpHost buildHttpHost(String host, int port, boolean isHttps) {
        return new HttpHost(host, port, isHttps ? HTTPS_SCHEME : HTTP_SCHEME);
    }

    public CloseableHttpClient buildClient(HttpHost httpHost, String username, String password) {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                new AuthScope(httpHost),
                new UsernamePasswordCredentials(username, password));
        return HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();
    }

    public HttpClientContext buildClientContext(HttpHost httpHost) {
        BasicAuthCache basicAuthCache = new BasicAuthCache();
        basicAuthCache.put(httpHost, new DigestScheme());
        HttpClientContext httpClientContext = HttpClientContext.create();
        httpClientContext.setAuthCache(basicAuthCache);
        httpClientContext.setTargetHost(httpHost);
        return httpClientContext;
    }
}
