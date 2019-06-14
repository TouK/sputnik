package pl.touk.sputnik.connector.http;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.touk.sputnik.configuration.ConfigurationBuilder;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.connector.ConnectorDetails;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.util.Properties;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;


class SslVerificationTest {

    private static final String LOCALHOST = "localhost";
    private static final String LOCALHOST_IP = "127.0.0.1";
    private WireMockServer trustedServer;
    private WireMockServer untrustedServer;

    @BeforeEach
    void setUp() {
        trustedServer = new WireMockServer(wireMockConfig()
                .dynamicPort()
                .dynamicHttpsPort()
                .keystorePath(this.getClass().getResource("/ssl/trusted/keystore.jks").getPath())
                .keystorePassword("changeit"));

        trustedServer.start();
        trustedServer.stubFor(get(urlEqualTo("/hello")).willReturn(aResponse().withStatus(200)));

        untrustedServer = new WireMockServer(wireMockConfig()
                .dynamicPort()
                .dynamicHttpsPort()
                .keystorePath(this.getClass().getResource("/ssl/untrusted/keystore.jks").getPath())
                .keystorePassword("changeit"));

        untrustedServer.start();
        untrustedServer.stubFor(get(urlEqualTo("/hello")).willReturn(aResponse().withStatus(200)));
    }

    @Test
    void doNotVerifySslTrustWhenVerificationIsOff() throws Exception {
        ConnectorDetails connectorDetails = buildConnectorDetails(LOCALHOST, untrustedServer.httpsPort(), "false");
        HttpHelper httpHelper = new HttpHelper();
        HttpHost httpHost = httpHelper.buildHttpHost(connectorDetails);
        CloseableHttpClient closeableHttpClient = httpHelper.buildClient(httpHost, connectorDetails);

        CloseableHttpResponse response = closeableHttpClient.execute(httpHost, new HttpGet("/hello"));

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
    }

    @Test
    void doNotVerifyHostnameWhenVerificationIsOff() throws Exception {
        ConnectorDetails connectorDetails = buildConnectorDetails(LOCALHOST_IP, trustedServer.httpsPort(), "false");
        HttpHelper httpHelper = new HttpHelper();
        HttpHost httpHost = httpHelper.buildHttpHost(connectorDetails);
        CloseableHttpClient closeableHttpClient = httpHelper.buildClient(httpHost, connectorDetails);

        CloseableHttpResponse response = closeableHttpClient.execute(httpHost, new HttpGet("/hello"));

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
    }

    @Test
    void verifySslTrust() throws Exception {
        setSystemTrustStore();
        ConnectorDetails connectorDetails = buildConnectorDetails(LOCALHOST, trustedServer.httpsPort(), "true");
        HttpHelper httpHelper = new HttpHelper();
        HttpHost httpHost = httpHelper.buildHttpHost(connectorDetails);
        CloseableHttpClient closeableHttpClient = httpHelper.buildClient(httpHost, connectorDetails);

        CloseableHttpResponse response = closeableHttpClient.execute(httpHost, new HttpGet("/hello"));

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
    }

    @Test
    void verifySslTrustThrowsSSLHandshakeException() throws Exception {
        ConnectorDetails connectorDetails = buildConnectorDetails(LOCALHOST, untrustedServer.httpsPort(), "true");
        HttpHelper httpHelper = new HttpHelper();
        HttpHost httpHost = httpHelper.buildHttpHost(connectorDetails);
        CloseableHttpClient closeableHttpClient = httpHelper.buildClient(httpHost, connectorDetails);

        Throwable thrown = catchThrowable(() -> closeableHttpClient.execute(httpHost, new HttpGet("/hello")));

        assertThat(thrown).isInstanceOf(SSLHandshakeException.class);
    }

    @Test
    void useDefaultTrustStoreToVerifySslTrust() throws Exception {
        ConnectorDetails connectorDetails = buildConnectorDetails(LOCALHOST, untrustedServer.httpsPort(), "true");
        HttpHelper httpHelper = new HttpHelper();
        HttpHost httpHost = httpHelper.buildHttpHost(connectorDetails);
        CloseableHttpClient closeableHttpClient = httpHelper.buildClient(httpHost, connectorDetails);

        Throwable thrown = catchThrowable(() -> closeableHttpClient.execute(httpHost, new HttpGet("/hello")));

        assertThat(thrown).isInstanceOf(SSLHandshakeException.class);
    }

    @Test
    void verifyHostnameThrowsSSLPeerUnverifiedExceptionWhenHostDoesNotMatch() throws Exception {
        setSystemTrustStore();
        ConnectorDetails connectorDetails = buildConnectorDetails(LOCALHOST_IP, trustedServer.httpsPort(), "true");
        HttpHelper httpHelper = new HttpHelper();
        HttpHost httpHost = httpHelper.buildHttpHost(connectorDetails);
        CloseableHttpClient closeableHttpClient = httpHelper.buildClient(httpHost, connectorDetails);

        Throwable thrown = catchThrowable(() -> closeableHttpClient.execute(httpHost, new HttpGet("/hello")));

        assertThat(thrown).isInstanceOf(SSLPeerUnverifiedException.class);
    }

    private void setSystemTrustStore() {
        System.setProperty("javax.net.ssl.trustStore", this.getClass().getResource("/ssl/trusted/cacerts.jks").getPath());
        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
    }

    private ConnectorDetails buildConnectorDetails(String host, int port, String verifySsl) {
        Properties properties = new Properties();
        properties.setProperty(GeneralOption.HOST.getKey(), host);
        properties.setProperty(GeneralOption.PORT.getKey(), Integer.toString(port));
        properties.setProperty(GeneralOption.USE_HTTPS.getKey(), "true");
        properties.setProperty(GeneralOption.VERIFY_SSL.getKey(), verifySsl);
        return new ConnectorDetails(ConfigurationBuilder.initFromProperties(properties));
    }

}