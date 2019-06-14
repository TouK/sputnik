package pl.touk.sputnik.connector.stash;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.touk.sputnik.HttpConnectorEnv;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationSetup;
import pl.touk.sputnik.connector.FacadeConfigUtil;
import pl.touk.sputnik.review.ReviewFile;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

class StashFacadeHttpsTest {

    private static String SOME_PULL_REQUEST_ID = "12314";
    private static String SOME_REPOSITORY = "repo";
    private static String SOME_PROJECT_KEY = "key";

    private static final ImmutableMap<String, String> STASH_PATCHSET_MAP = ImmutableMap.of(
            "cli.pullRequestId", SOME_PULL_REQUEST_ID,
            "connector.repository", SOME_REPOSITORY,
            "connector.project", SOME_PROJECT_KEY
    );

    private StashFacade stashFacade;
    private WireMockServer wireMockServer;
    private HttpConnectorEnv httpConnectorEnv;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(wireMockConfig().port(FacadeConfigUtil.HTTP_PORT).httpsPort(FacadeConfigUtil.HTTPS_PORT));
        wireMockServer.start();

        httpConnectorEnv = new HttpConnectorEnv(wireMockServer);

        Configuration config = new ConfigurationSetup().setUp(FacadeConfigUtil.getHttpsConfig("stash"), STASH_PATCHSET_MAP);
        stashFacade = new StashFacadeBuilder().build(config);
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void shouldGetChangeInfo() throws Exception {
        wireMockServer.stubFor(get(urlEqualTo(String.format(
                "%s/rest/api/1.0/projects/%s/repos/%s/pull-requests/%s/changes",
                FacadeConfigUtil.PATH, SOME_PROJECT_KEY, SOME_REPOSITORY, SOME_PULL_REQUEST_ID)))
                .withHeader("Authorization", equalTo("Basic dXNlcjpwYXNz"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(IOUtils.toString(getClass().getResourceAsStream("/json/stash-changes.json")))));

        List<ReviewFile> files = stashFacade.listFiles();

        assertThat(files).hasSize(4);
    }
}
