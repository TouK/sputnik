package pl.touk.sputnik.connector.http;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.touk.sputnik.connector.ConnectorDetails;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Slf4j
public class HttpHelper {
    private static final String HTTP_SCHEME = "http";
    private static final String HTTPS_SCHEME = "https";
    private static final TrustStrategy TRUST_ALL_STRATEGY = new TrustStrategy() {
        @Override
        public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            return true;
        }
    };

    @NotNull
    public HttpHost buildHttpHost(@NotNull ConnectorDetails connectorDetails) {
        return new HttpHost(connectorDetails.getHost(), connectorDetails.getPort(), connectorDetails.isHttps() ? HTTPS_SCHEME : HTTP_SCHEME);
    }

    @NotNull
    public String buildHttpHostUri(@NotNull ConnectorDetails connectorDetails) {
        return buildHttpHost(connectorDetails).toURI();
    }

    @NotNull
    public CloseableHttpClient buildClient(@NotNull HttpHost httpHost, @NotNull ConnectorDetails connectorDetails) {
        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        httpClientBuilder.setDefaultCredentialsProvider(buildBasicCredentialsProvider(httpHost, connectorDetails.getUsername(), connectorDetails.getPassword()));
        if (connectorDetails.isHttps()) {
            httpClientBuilder.setSSLSocketFactory(buildSSLSocketFactory(connectorDetails));
        }
        return httpClientBuilder.build();
    }

    @NotNull
    public HttpClientContext buildClientContext(@NotNull HttpHost httpHost, @NotNull AuthScheme authScheme) {
        AuthCache basicAuthCache = new BasicAuthCache();
        basicAuthCache.put(httpHost, authScheme);
        HttpClientContext httpClientContext = HttpClientContext.create();
        httpClientContext.setAuthCache(basicAuthCache);
        httpClientContext.setTargetHost(httpHost);
        return httpClientContext;
    }

    @NotNull
    private CredentialsProvider buildBasicCredentialsProvider(@NotNull HttpHost httpHost, @NotNull String username, @NotNull String password) {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                new AuthScope(httpHost),
                new UsernamePasswordCredentials(username, password));
        return credentialsProvider;
    }

    @Nullable
    public LayeredConnectionSocketFactory buildSSLSocketFactory(ConnectorDetails connectorDetails) {
        if (connectorDetails.isVerifySsl()) {
            return SSLConnectionSocketFactory.getSocketFactory();
        }
        try {
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, TRUST_ALL_STRATEGY).build();
            return new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            log.error("Error building SSL socket factory", e);
            throw new IllegalStateException(e);
        }
    }
}
