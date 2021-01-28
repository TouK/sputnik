package pl.touk.sputnik.connector.gerrit;

import com.google.common.annotations.VisibleForTesting;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GerritOptions {
    /**
     * Indicates whether to use Gerrit's internal password token.
     */
    private final boolean useHttpPassword;
    /**
     * Indicates whether to avoid publishing the same comment again when the review is retriggered
     * for the same revision.
     */
    private final boolean omitDuplicateComments;

    static GerritOptions from(Configuration configuration) {
        return new GerritOptions(
                Boolean.parseBoolean(configuration.getProperty(GeneralOption.GERRIT_USE_HTTP_PASSWORD)),
                Boolean.parseBoolean(configuration.getProperty(GeneralOption.GERRIT_OMIT_DUPLICATE_COMMENTS)));
    }

    @VisibleForTesting
    static GerritOptions empty() {
        return new GerritOptions(false, false);
    }
}
