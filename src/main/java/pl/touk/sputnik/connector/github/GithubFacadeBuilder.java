package pl.touk.sputnik.connector.github;

import com.jcabi.github.Coordinates;
import com.jcabi.github.Github;
import com.jcabi.github.Repo;
import com.jcabi.github.RtGithub;
import com.jcabi.http.wire.RetryWire;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.connector.Patchset;
import pl.touk.sputnik.connector.PatchsetBuilder;

@Slf4j
public class GithubFacadeBuilder {

    @NotNull
    public GithubFacade build(Configuration configuration) {

        Patchset patchset = PatchsetBuilder.build(configuration);

        String oAuthKey = configuration.getProperty(GeneralOption.GITHUB_API_KEY);
        Github github = new RtGithub(
                new RtGithub(oAuthKey)
                        .entry()
                        .through(RetryWire.class)
        );

        Repo repo = github.repos().get(new Coordinates.Simple(patchset.getProjectPath()));
        return new GithubFacade(repo, patchset);
    }
}
