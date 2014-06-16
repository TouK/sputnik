package pl.touk.sputnik.connector.stash;

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
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;

public class StashFacadeTest {

    private static Integer PORT = 8089;
    private static String SOME_PULL_REQUEST_ID = "12314";
    private static String SOME_REPOSITORY = "repo";
    private static String SOME_PROJECT_KEY = "key";

    private static final Map<String, String> STASH_CONFIG_MAP = ImmutableMap.of(
            "stash.host", "localhost",
            "stash.port", PORT.toString(),
            "stash.username", "user",
            "stash.password", "pass",
            "stash.useHttps", "false"
    );
    private static final Map<String, String> STASH_PATCHSET_MAP = ImmutableMap.of(
            "cli.pullRequestId", SOME_PULL_REQUEST_ID,
            "stash.repositorySlug", SOME_REPOSITORY,
            "stash.projectKey", SOME_PROJECT_KEY
    );

    private StashFacade fixture;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(PORT);

    @Before
    public void setUp() {
        new ConfigurationSetup().setUp(STASH_CONFIG_MAP, STASH_PATCHSET_MAP);
        fixture = new StashFacadeBuilder().build();
    }

    @Test
    public void shouldGetChangeInfo() throws Exception {
        String url = String.format(
                "/rest/api/1.0/projects/%s/repos/%s/pull-requests/%s/changes",
                SOME_PROJECT_KEY, SOME_REPOSITORY, SOME_PULL_REQUEST_ID
        );
        stubFor(get(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(IOUtils.toString(getClass().getResourceAsStream("/json/stash-changes.json")))));

        List<ReviewFile> files = fixture.listFiles();
        assertThat(files).hasSize(4);
    }

    @Test
    public void shouldReturnDiffAsMapOfLines() throws Exception {
        stubFor(get(urlMatching("/rest/api/1.0/projects/mykey/repos/myproject/pull-requests/12/diff.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(IOUtils.toString(getClass().getResourceAsStream("/json/stash-diff.json")))));

        SingleFileChanges singleFileChanges = fixture.changesForSingleFile("src/main/java/Main.java");
        assertThat(singleFileChanges.getFilename()).isEqualTo("src/main/java/Main.java");
        assertThat(singleFileChanges.getChangesMap())
                .containsEntry(1, ChangeType.ADDED)
                .containsEntry(2, ChangeType.ADDED);
    }
}
