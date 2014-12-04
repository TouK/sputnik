package pl.touk.sputnik.processor.checkstyle;

import com.google.common.collect.ImmutableList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pl.touk.sputnik.configuration.ConfigurationHolder;
import pl.touk.sputnik.review.Review;
import pl.touk.sputnik.review.ReviewResult;
import pl.touk.sputnik.review.filter.FileFilter;
import pl.touk.sputnik.review.transformer.IOFileTransformer;

import java.io.File;
import java.util.List;

import static com.google.common.io.Resources.getResource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CheckstyleProcessorTest {

    private final CheckstyleProcessor fixture = new CheckstyleProcessor();

    @Mock
    private Review review;

    @Before
    public void setUp() throws Exception {
        ConfigurationHolder.initFromResource("test.properties");
    }

    @After
    public void tearDown() throws Exception {
        ConfigurationHolder.reset();
    }

    @Test
    public void shouldReturnBasicSunViolationsOnSimpleClass() {
        //given
        List<File> file = ImmutableList.of(getResourceAsFile("TestFile.java"));
        when(review.getFiles(any(FileFilter.class), any(IOFileTransformer.class))).thenReturn(file);

        //when
        ReviewResult reviewResult = fixture.process(review);

        //then
        assertThat(reviewResult).isNotNull();
        assertThat(reviewResult.getViolations())
                .isNotEmpty()
                .hasSize(3)
                .extracting("message")
                .containsOnly(
                        "Missing package-info.java file.",
                        "Missing a Javadoc comment."
                );
    }

    private File getResourceAsFile(String resourceName) {
        return new File(getResource(resourceName).getFile());
    }

}
