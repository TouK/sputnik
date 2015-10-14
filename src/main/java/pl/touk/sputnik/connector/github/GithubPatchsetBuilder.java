package pl.touk.sputnik.connector.github;

import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.CliOption;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notBlank;

public class GithubPatchsetBuilder {

    @NotNull
    public static GithubPatchset build(Configuration configuration) {
        String pullRequestId = configuration.getProperty(CliOption.PULL_REQUEST_ID);
        String project = configuration.getProperty(GeneralOption.PROJECT);
        String repository = configuration.getProperty(GeneralOption.REPOSITORY);

        notBlank(pullRequestId, "You must provide non blank Github pull request id");
        isTrue(NumberUtils.isNumber(pullRequestId), "Integer value as pull request id required");
        notBlank(project, "You must provide non blank Github project key");
        notBlank(repository, "You must provide non blank Github repository slug");

        return new GithubPatchset(Integer.parseInt(pullRequestId), String.format("%s/%s", project, repository));
    }

}
