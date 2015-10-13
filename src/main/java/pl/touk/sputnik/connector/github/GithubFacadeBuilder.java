package pl.touk.sputnik.connector.github;

import com.jcabi.github.Coordinates;
import com.jcabi.github.Github;
import com.jcabi.github.Repo;
import com.jcabi.github.RtGithub;
import com.jcabi.http.wire.RetryWire;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.CliOption;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notBlank;

@Slf4j
public class GithubFacadeBuilder {

    @NotNull
    public GithubFacade build(Configuration configuration) {

        GithubPatchset githubPatchset = buildGithubPatchset(configuration);

        String oAuthKey = configuration.getProperty(GeneralOption.GITHUB_API_KEY);
        Github github = new RtGithub(
                new RtGithub(oAuthKey)
                        .entry()
                        .through(RetryWire.class)
        );

        Repo repo = github.repos().get(new Coordinates.Simple(githubPatchset.getProjectPath()));
        return new GithubFacade(repo, githubPatchset);
    }

    @NotNull
    public GithubPatchset buildGithubPatchset(Configuration configuration) {
        String pullRequestId = configuration.getProperty(CliOption.PULL_REQUEST_ID);
        String repositorySlug = configuration.getProperty(GeneralOption.REPOSITORY);
        String projectKey = configuration.getProperty(GeneralOption.OWNER);

        notBlank(pullRequestId, "You must provide non blank Github pull request id");
        isTrue(NumberUtils.isNumber(pullRequestId), "Integer value as pull request id required");
        notBlank(repositorySlug, "You must provide non blank Github repository slug");
        notBlank(projectKey, "You must provide non blank Github project key");

        return new GithubPatchset(Integer.parseInt(pullRequestId), String.format("%s/%s", repositorySlug, projectKey));
    }
}
