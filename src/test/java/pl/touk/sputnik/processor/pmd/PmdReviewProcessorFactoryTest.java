package pl.touk.sputnik.processor.pmd;

import org.junit.jupiter.api.Test;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.ReviewProcessorFactory;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static pl.touk.sputnik.SputnikAssertions.assertThat;

class PmdReviewProcessorFactoryTest {

    @Test
    void testIsEnabled() {
        Configuration configuration = mock(Configuration.class);
        when(configuration.getProperty(eq(GeneralOption.PMD_ENABLED))).thenReturn("true");

        ReviewProcessorFactory factory = new PmdReviewProcessorFactory();
        assertThat(factory.isEnabled(configuration)).isTrue();
    }

    @Test
    void testCreate() {
        Configuration configuration = mock(Configuration.class);
        when(configuration.getProperty(eq(GeneralOption.PMD_ENABLED))).thenReturn("true");

        ReviewProcessorFactory factory = new PmdReviewProcessorFactory();
        assertThat(factory.create(configuration)).isNotNull();
    }
}