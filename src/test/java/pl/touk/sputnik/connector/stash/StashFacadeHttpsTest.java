package pl.touk.sputnik.connector.stash;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import pl.touk.sputnik.configuration.ConfigurationSetup;
import pl.touk.sputnik.connector.FacadeConfigUtil;
import pl.touk.sputnik.review.ReviewFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

public class StashFacadeHttpsTest {

    private static final ImmutableMap<String, String> STASH_PATCHSET_MAP = ImmutableMap.of(
            "cli.pullRequestId", "12",
            "connector.repositorySlug", "myproject",
            "connector.projectKey", "mykey"
    );

    private StashFacade stashFacade;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(FacadeConfigUtil.HTTP_PORT, FacadeConfigUtil.HTTPS_PORT);

    @Before
    public void setUp() {
        new ConfigurationSetup().setUp(FacadeConfigUtil.getHttpsConfig("stash"), STASH_PATCHSET_MAP);
        stashFacade = new StashFacadeBuilder().build();
    }

    @Test
    public void shouldGetChangeInfo() throws Exception {
        String changesUrl = "/rest/api/1.0/projects/mykey/repos/myproject/pull-requests/12/changes";
        stubFor(get(urlEqualTo(changesUrl))
                .withHeader("Authorization", equalTo("Basic dXNlcjpwYXNz"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(IOUtils.toString(getClass().getResourceAsStream("/json/stash-changes.json")))));

        List<ReviewFile> files = stashFacade.listFiles();
        assertThat(files).hasSize(4);
    }
}
