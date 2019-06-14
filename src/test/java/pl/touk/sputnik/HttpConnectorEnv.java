package pl.touk.sputnik;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;

@AllArgsConstructor
public class HttpConnectorEnv {

    private WireMockServer wireMockServer;

    public void stubGet(UrlPattern url, ResponseDefinitionBuilder responseDefinitionBuilder) {
        wireMockServer.stubFor(get(url)
                .withHeader("Authorization", equalTo("Basic dXNlcjpwYXNz"))
                .willReturn(responseDefinitionBuilder));
    }

    public void stubGet(UrlPattern url, String responseFile) throws Exception {
        wireMockServer.stubFor(get(url)
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBody(IOUtils.toString(getClass().getResourceAsStream(responseFile)))));
    }

    public void stubPost(UrlPattern url, String responseFile) throws Exception {
        wireMockServer.stubFor(post(url)
                .withHeader("Authorization", equalTo("Basic dXNlcjpwYXNz"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBody(IOUtils.toString(getClass().getResourceAsStream(responseFile)))));
    }
}
