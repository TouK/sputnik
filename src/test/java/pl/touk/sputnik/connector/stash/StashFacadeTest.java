package pl.touk.sputnik.connector.stash;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import pl.touk.sputnik.configuration.ConfigurationSetup;
import pl.touk.sputnik.review.ReviewFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

public class StashFacadeTest {

    private static Integer PORT = 8089;
    private static final ImmutableMap<String, String> STASH_CONFIG_MAP = ImmutableMap.of(
            "stash.host", "localhost",
            "stash.port", PORT.toString(),
            "stash.username", "user",
            "stash.password", "pass",
            "stash.useHttps", "false"
    );
    private static final ImmutableMap<String, String> STASH_PATCHSET_MAP = ImmutableMap.of(
            "cli.pullRequestId", "12",
            "stash.repositorySlug", "myproject",
            "stash.projectKey", "mykey"
    );

    private StashFacade stashFacade;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(PORT);

    @Before
    public void setUp() {
        Map<String, String> joinedMap = new HashMap<>();
        joinedMap.putAll(STASH_CONFIG_MAP);
        joinedMap.putAll(STASH_PATCHSET_MAP);
        new ConfigurationSetup().setUp(joinedMap);
        stashFacade = new StashFacadeBuilder().build();
    }

    @Test
    public void shouldGetChangeInfo() throws Exception {
        stubFor(get(urlEqualTo("/rest/api/1.0/projects/mykey/repos/myproject/pull-requests/12/changes"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(IOUtils.toString(getClass().getResourceAsStream("/json/stash-changes.json")))));

        List<ReviewFile> files = stashFacade.listFiles();
        assertThat(files).hasSize(4);
    }

    @Test
    public void shouldReturnDiffAsMapOfLines() throws Exception {
        stubFor(get(urlMatching("/rest/api/1.0/projects/mykey/repos/myproject/pull-requests/12/diff.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(IOUtils.toString(getClass().getResourceAsStream("/json/stash-diff.json")))));

        SingleFileChanges singleFileChanges = stashFacade.changesForSingleFile("src/main/java/Main.java");
        assertThat(singleFileChanges.getFilename()).isEqualTo("src/main/java/Main.java");
        assertThat(singleFileChanges.getChangesMap())
                .containsEntry(1, ChangeType.ADDED)
                .containsEntry(2, ChangeType.ADDED);
    }
}
