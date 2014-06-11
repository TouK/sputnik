package pl.touk.sputnik.connector.gerrit;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import pl.touk.sputnik.review.ReviewFile;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;

public class GerritFacadeTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(PORT);

    private static int PORT = 8089;
    private GerritFacade fixture = new GerritFacade(new GerritConnector("localhost", PORT, "user", "pass", false));

    @Test
    public void shouldGetChangeInfo() throws Exception {
        //given
        GerritPatchset patchset = new GerritPatchset("a", "c");
        String url = GerritConnector.createUrl(patchset, GerritConnector.GET_LIST_FILES_URL_FORMAT);
        stubFor(get(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(IOUtils.toString(getClass().getResourceAsStream("/gerrit-changes.json")))));

        //when
        List<ReviewFile> files = fixture.listFiles(patchset);

        //then
        assertThat(files).hasSize(2);
        verify(getRequestedFor(urlMatching(url)));
    }

}
