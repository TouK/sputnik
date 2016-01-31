package pl.touk.sputnik.processor.jslint;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.ReviewProcessorFactory;
import pl.touk.sputnik.processor.findbugs.FindbugsReviewProcessorFactory;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JsLintReviewProcessorFactoryTest {

    @Test
    public void testIsEnabled() throws Exception {
        Configuration configuration = mock(Configuration.class);
        when(configuration.getProperty(eq(GeneralOption.JSLINT_ENABLED))).thenReturn("true");

        ReviewProcessorFactory factory = new JsLintReviewProcessorFactory();
        assertTrue(factory.isEnabled(configuration));
    }

    @Test
    public void testCreate() throws Exception {
        Configuration configuration = mock(Configuration.class);
        when(configuration.getProperty(eq(GeneralOption.JSLINT_ENABLED))).thenReturn("true");

        ReviewProcessorFactory factory = new JsLintReviewProcessorFactory();
        assertNotNull(factory.create(configuration));
    }
}