package pl.touk.sputnik.connector.stash;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import pl.touk.sputnik.review.ReviewFile;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

public class StashFacadeTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(PORT);

    private static int PORT = 8089;
    private StashFacade fixture = new StashFacade("localhost", PORT, "user", "pass", false);

    @Test
    public void shouldGetChangeInfo() throws Exception {
        StashPatchset patchset = new StashPatchset("a", "b", "c");
        String url = StashConnector.createUrl(patchset, StashConnector.CHANGES_URL_FORMAT);
        stubFor(get(urlEqualTo(StashConnector.createUrl(patchset, StashConnector.CHANGES_URL_FORMAT)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(IOUtils.toString(getClass().getResourceAsStream("/stash-changes.json")))));

        List<ReviewFile> files = fixture.listFiles(patchset);

        assertThat(files).hasSize(3);

        verify(getRequestedFor(urlMatching(url)));
    }
}
