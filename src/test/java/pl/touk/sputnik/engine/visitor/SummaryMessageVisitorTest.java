package pl.touk.sputnik.engine.visitor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.touk.sputnik.review.Review;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SummaryMessageVisitorTest {

    private static final String TOTAL_8_VIOLATIONS_FOUND = "Total 8 violations found";
    private static final String PROBLEM_SOURCE = "PMD";
    private static final String PROBLEM_MESSAGE = "configuration error";
    private static final String PROBLEM_FORMATTED_MESSAGE = "There is a problem with PMD: configuration error";

    private final SummaryMessageVisitor summaryMessageVisitor = new SummaryMessageVisitor("Perfect");

    @Mock
    private Review review;
    private List<String> messages = new ArrayList<>();

    @BeforeEach
    void setUp() {
        when(review.getMessages()).thenReturn(messages);
    }

    @Test
    void shouldAddSummaryMessage() {
        when(review.getTotalViolationCount()).thenReturn(8L);

        summaryMessageVisitor.afterReview(review);

        assertThat(review.getMessages()).containsOnly(TOTAL_8_VIOLATIONS_FOUND);
    }

    @Test
    void shouldAddSummaryMessageWithOneViolation() {
        when(review.getTotalViolationCount()).thenReturn(1L);

        summaryMessageVisitor.afterReview(review);

        assertThat(review.getMessages()).containsOnly("Total 1 violation found");
    }

    @Test
    void shouldAddPerfectMessageIfThereAreNoViolationsFound() {
        when(review.getTotalViolationCount()).thenReturn(0L);

        summaryMessageVisitor.afterReview(review);

        assertThat(review.getMessages()).containsOnly("Perfect");
    }

    @Test
    void shouldAddProblemMessagesPerfectMessageIfThereAreNoViolationsFound() {
        when(review.getTotalViolationCount()).thenReturn(8L);
        when(review.getProblems()).thenReturn(singletonList("There is a problem with PMD: configuration error"));

        summaryMessageVisitor.afterReview(review);

        assertThat(review.getMessages()).containsSequence(TOTAL_8_VIOLATIONS_FOUND, PROBLEM_FORMATTED_MESSAGE);
    }

}
