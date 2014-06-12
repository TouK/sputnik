package pl.touk.sputnik.connector.gerrit;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import pl.touk.sputnik.configuration.ConfigurationSetup;
import pl.touk.sputnik.review.ReviewFile;

import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;

public class GerritFacadeTest {

    private static Integer PORT = 8089;
    private static String SOME_CHANGE_ID = "12314";
    private static String SOME_REVISION_ID = "12314";

    private static final Map<String, String> GERRIT_CONFIG_MAP = ImmutableMap.of(
            "gerrit.host", "localhost",
            "gerrit.port", PORT.toString(),
            "gerrit.username", "user",
            "gerrit.password", "pass",
            "gerrit.useHttps", "false"
    );

    private static final Map<String, String> GERRIT_PATCHSET_MAP = ImmutableMap.of(
            "cli.changeId", SOME_CHANGE_ID,
            "cli.revisionId", SOME_REVISION_ID,
            "gerrit.projectKey", "mykey"
    );

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(PORT);

    private GerritFacade fixture;

    @Before
    public void setUp() {
        new ConfigurationSetup().setUp(GERRIT_CONFIG_MAP, GERRIT_PATCHSET_MAP);
        fixture = new GerritFacadeBuilder().build();
    }
    @Test
    public void shouldGetChangeInfo() throws Exception {
        //given
        String url = String.format("/a/changes/%s/revisions/%s/files/", SOME_CHANGE_ID, SOME_REVISION_ID);
        stubFor(get(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(IOUtils.toString(getClass().getResourceAsStream("/gerrit-changes.json")))));

        //when
        List<ReviewFile> files = fixture.listFiles();

        //then
        assertThat(files).hasSize(2);
        verify(getRequestedFor(urlMatching(url)));
    }

}
