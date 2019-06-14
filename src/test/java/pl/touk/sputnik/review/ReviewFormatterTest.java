package pl.touk.sputnik.review;

import org.junit.jupiter.api.Test;
import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.GeneralOption;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReviewFormatterTest {

    @Test
    void shouldFormatProblemsAndComments() {
        Configuration configMock = mock(Configuration.class);
        when(configMock.getProperty(GeneralOption.MESSAGE_COMMENT_FORMAT)).thenReturn("{0}{1}{2}");
        when(configMock.getProperty(GeneralOption.MESSAGE_PROBLEM_FORMAT)).thenReturn("{0}{1}");

        ReviewFormatter formatter = new ReviewFormatter(configMock);

        assertThat(formatter.formatComment("source/", Severity.ERROR, "/message")).isEqualTo("source/ERROR/message");
        assertThat(formatter.formatProblem("source/", "message")).isEqualTo("source/message");
    }

    @Test
    void shouldPartiallyFormat() {
        Configuration configMock = mock(Configuration.class);
        when(configMock.getProperty(GeneralOption.MESSAGE_COMMENT_FORMAT)).thenReturn("{0}{2}");
        when(configMock.getProperty(GeneralOption.MESSAGE_PROBLEM_FORMAT)).thenReturn("{0}{1}");

        ReviewFormatter formatter = new ReviewFormatter(configMock);

        assertThat(formatter.formatComment("source/", Severity.ERROR, "message")).isEqualTo("source/message");
        assertThat(formatter.formatProblem("source/", null)).isEqualTo("source/");
    }
}
