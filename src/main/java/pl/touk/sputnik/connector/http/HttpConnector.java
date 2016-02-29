package pl.touk.sputnik.connector.http;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@AllArgsConstructor
@Getter
public class HttpConnector {
    private static int REQUEST_COUNTER = 0;

    private CloseableHttpClient httpClient;
    private HttpClientContext httpClientContext;
    private String contextPath = StringUtils.EMPTY;

    @NotNull
    public URI buildUri(String path, NameValuePair... parameters) throws URISyntaxException {
        HttpHost targetHost = httpClientContext.getTargetHost();

        return new URIBuilder()
                .setHost(targetHost.getHostName())
                .setPort(targetHost.getPort())
                .setScheme(targetHost.getSchemeName())
                .setPath(contextPath + path)
                .setParameters(parameters)
                .build();
    }

    @NotNull
    public CloseableHttpResponse logAndExecute(@NotNull HttpRequestBase request) throws IOException {
        log.info("Request  {}: {} to {}", ++REQUEST_COUNTER, request.getMethod(), request.getURI().toString());
        CloseableHttpResponse httpResponse = httpClient.execute(request, httpClientContext);
        log.info("Response {}: {}", REQUEST_COUNTER, httpResponse.getStatusLine().toString());
        return httpResponse;
    }

    @NotNull
    public String consumeAndLogEntity(@NotNull CloseableHttpResponse response) throws IOException {
        if (!isSuccessful(response)) {
            throw new HttpException(response);
        }
        if (response.getEntity() == null) {
            log.debug("Entity {}: no entity", REQUEST_COUNTER);
            return StringUtils.EMPTY;
        }
        String content = EntityUtils.toString(response.getEntity());
        log.info("Entity {}: {}", REQUEST_COUNTER, content);
        return content;
    }

    private boolean isSuccessful(HttpResponse response) {
        return response.getStatusLine().getStatusCode() / 100 == 2;
    }
}
