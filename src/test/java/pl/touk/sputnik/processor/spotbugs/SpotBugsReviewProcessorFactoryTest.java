package pl.touk.sputnik.processor.spotbugs;

import org.junit.jupiter.api.Test;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.ReviewProcessorFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SpotBugsReviewProcessorFactoryTest {

    private static final String TRUE = "true";

    private ReviewProcessorFactory factory = new SpotBugsReviewProcessorFactory();

    @Test
    void shouldEnableSpotBugsWithFindBugsEnabled() {
        Configuration configuration = mock(Configuration.class);
        when(configuration.getProperty(GeneralOption.FINDBUGS_ENABLED)).thenReturn(TRUE);

        assertThat(factory.isEnabled(configuration)).isTrue();
    }

    @Test
    void shouldEnableSpotBugsWithSpotBugsEnabled() {
        Configuration configuration = mock(Configuration.class);
        when(configuration.getProperty(GeneralOption.SPOTBUGS_ENABLED)).thenReturn(TRUE);

        assertThat(factory.isEnabled(configuration)).isTrue();
    }
}
