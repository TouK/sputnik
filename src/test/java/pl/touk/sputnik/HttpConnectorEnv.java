package pl.touk.sputnik;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.UrlMatchingStrategy;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

public class HttpConnectorEnv {

    protected void stubGet(UrlMatchingStrategy url, ResponseDefinitionBuilder responseDefinitionBuilder) {
        stubFor(get(url)
                .withHeader("Authorization", equalTo("Basic dXNlcjpwYXNz"))
                .willReturn(responseDefinitionBuilder));
    }

    protected void stubGet(UrlMatchingStrategy url, String responseFile) throws Exception {
        stubGet(url, aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBody(IOUtils.toString(getClass().getResourceAsStream(responseFile))));
    }

    protected void stubPost(UrlMatchingStrategy url, String responseFile) throws Exception {
        stubFor(post(url)
                .withHeader("Authorization", equalTo("Basic dXNlcjpwYXNz"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBody(IOUtils.toString(getClass().getResourceAsStream(responseFile)))));
    }
}
