package pl.touk.sputnik.connector.stash;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationSetup;
import pl.touk.sputnik.connector.FacadeConfigUtil;
import pl.touk.sputnik.review.ReviewFile;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

public class StashFacadeHttpsTest {

    private static String SOME_PULL_REQUEST_ID = "12314";
    private static String SOME_REPOSITORY = "repo";
    private static String SOME_PROJECT_KEY = "key";

    private static final ImmutableMap<String, String> STASH_PATCHSET_MAP = ImmutableMap.of(
            "cli.pullRequestId", SOME_PULL_REQUEST_ID,
            "connector.repositorySlug", SOME_REPOSITORY,
            "connector.projectKey", SOME_PROJECT_KEY
    );

    private StashFacade stashFacade;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(FacadeConfigUtil.HTTP_PORT, FacadeConfigUtil.HTTPS_PORT);

    @Before
    public void setUp() {
        Configuration config = new ConfigurationSetup().setUp(FacadeConfigUtil.getHttpsConfig("stash"), STASH_PATCHSET_MAP);
        stashFacade = new StashFacadeBuilder().build(config);
    }

    @Test
    public void shouldGetChangeInfo() throws Exception {
        stubFor(get(urlEqualTo(String.format(
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
