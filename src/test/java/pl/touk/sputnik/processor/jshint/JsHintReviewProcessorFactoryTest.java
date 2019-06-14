package pl.touk.sputnik.processor.jshint;

import org.junit.jupiter.api.Test;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.ReviewProcessorFactory;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static pl.touk.sputnik.SputnikAssertions.assertThat;

class JsHintReviewProcessorFactoryTest {

    @Test
    void testIsEnabled() throws Exception {
        Configuration configuration = mock(Configuration.class);
        when(configuration.getProperty(eq(GeneralOption.JSHINT_ENABLED))).thenReturn("true");

        ReviewProcessorFactory factory = new JsHintReviewProcessorFactory();
        assertThat(factory.isEnabled(configuration)).isTrue();
    }

    @Test
    void testCreate() throws Exception {
        Configuration configuration = mock(Configuration.class);
        when(configuration.getProperty(eq(GeneralOption.JSHINT_ENABLED))).thenReturn("true");

        ReviewProcessorFactory factory = new JsHintReviewProcessorFactory();
        assertThat(factory.create(configuration)).isNotNull();
    }
}