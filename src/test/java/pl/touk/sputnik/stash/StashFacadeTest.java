package pl.touk.sputnik.stash;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;
import pl.touk.sputnik.review.ReviewFile;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class StashFacadeTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(PORT);

    private static int PORT = 8089;
    private StashFacade fixture = new StashFacade("localhost", PORT, "user", "pass");

    @Test
    public void shouldGetChangeInfo() throws Exception {
        StashPatchset patchset = new StashPatchset("a", "b", "c");
        String url = StashConnector.createChangesUrl(patchset);
        stubFor(get(urlEqualTo(StashConnector.createChangesUrl(patchset)))
                //.withHeader("Authorization", equalTo("Basic asdasd"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(IOUtils.toString(getClass().getResourceAsStream("/stash-changes.json")))));

        List<ReviewFile> files = fixture.listFiles(patchset);

        assertEquals(3, files.size());

        verify(getRequestedFor(urlMatching(url)));
    }
}
