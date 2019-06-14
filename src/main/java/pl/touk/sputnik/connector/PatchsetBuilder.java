package pl.touk.sputnik.connector;

import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.configuration.CliOption;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.configuration.Provider;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

public final class PatchsetBuilder {

    private PatchsetBuilder() { }

    @NotNull
    public static Patchset build(Configuration configuration) {
        String pullRequestId = configuration.getProperty(CliOption.PULL_REQUEST_ID);
        String project = configuration.getProperty(GeneralOption.PROJECT);
        String repository = configuration.getProperty(GeneralOption.REPOSITORY);
        Provider provider = Provider.from(configuration.getProperty(CliOption.PROVIDER));

        notBlank(pullRequestId, "You must provide non blank pull request id");
        isTrue(NumberUtils.isCreatable(pullRequestId), "Integer value as pull request id required");
        notBlank(project, "You must provide non blank project key");
        notBlank(repository, "You must provide non blank repository slug");
        notNull(provider, "You must provide non blank SCM provider");

        return new Patchset(Integer.parseInt(pullRequestId), String.format("%s/%s", project, repository), provider);
    }

}
