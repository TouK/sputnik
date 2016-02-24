package pl.touk.sputnik.processor.sonar;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;
import pl.touk.sputnik.processor.ReviewProcessorFactory;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SonarReviewProcessorFactoryTest {

    @Test
    public void testIsEnabled() throws Exception {
        Configuration configuration = mock(Configuration.class);
        when(configuration.getProperty(eq(GeneralOption.SONAR_ENABLED))).thenReturn("true");

        ReviewProcessorFactory factory = new SonarReviewProcessorFactory();
        assertTrue(factory.isEnabled(configuration));
    }

    @Test
    public void testCreate() throws Exception {
        Configuration configuration = mock(Configuration.class);
        when(configuration.getProperty(eq(GeneralOption.SONAR_ENABLED))).thenReturn("true");

        ReviewProcessorFactory factory = new SonarReviewProcessorFactory();
        assertNotNull(factory.create(configuration));
    }
}