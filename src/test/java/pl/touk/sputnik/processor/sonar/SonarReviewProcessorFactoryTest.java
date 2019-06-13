package pl.touk.sputnik.processor.sonar;

import org.junit.jupiter.api.Test;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.ReviewProcessorFactory;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static pl.touk.sputnik.SputnikAssertions.assertThat;

class SonarReviewProcessorFactoryTest {

    @Test
    void testIsEnabled() {
        Configuration configuration = mock(Configuration.class);
        when(configuration.getProperty(eq(GeneralOption.SONAR_ENABLED))).thenReturn("true");

        ReviewProcessorFactory factory = new SonarReviewProcessorFactory();
        assertThat(factory.isEnabled(configuration)).isTrue();
    }

    @Test
    void testCreate() {
        Configuration configuration = mock(Configuration.class);
        when(configuration.getProperty(eq(GeneralOption.SONAR_ENABLED))).thenReturn("true");

        ReviewProcessorFactory factory = new SonarReviewProcessorFactory();
        assertThat(factory.create(configuration)).isNotNull();
    }
}