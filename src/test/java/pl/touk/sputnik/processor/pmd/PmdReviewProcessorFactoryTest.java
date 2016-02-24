package pl.touk.sputnik.processor.pmd;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.ReviewProcessorFactory;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PmdReviewProcessorFactoryTest {

    @Test
    public void testIsEnabled() throws Exception {
        Configuration configuration = mock(Configuration.class);
        when(configuration.getProperty(eq(GeneralOption.PMD_ENABLED))).thenReturn("true");

        ReviewProcessorFactory factory = new PmdReviewProcessorFactory();
        assertTrue(factory.isEnabled(configuration));
    }

    @Test
    public void testCreate() throws Exception {
        Configuration configuration = mock(Configuration.class);
        when(configuration.getProperty(eq(GeneralOption.PMD_ENABLED))).thenReturn("true");

        ReviewProcessorFactory factory = new PmdReviewProcessorFactory();
        assertNotNull(factory.create(configuration));
    }
}