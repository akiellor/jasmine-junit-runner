package jasmine.junit;

import jasmine.StackTraceAsserts;
import jasmine.runtime.It;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JUnitNotifierTest {
    @Mock RunNotifier runNotifier;
    @Mock It it;
    @Mock Exception exception;
    @Mock jasmine.runtime.Failure failure;

    @Test
    public void shouldDelegateToJunitRunNotifierForStarted() {
        when(it.getDescription()).thenReturn("Object");
        when(it.getId()).thenReturn("test");

        new JUnitNotifier(runNotifier).started(it);

        verify(runNotifier).fireTestStarted(Description.createSuiteDescription("Object", "test"));
    }

    @Test
    public void shouldDelegateToJunitRunNotifierForPass() {
        when(it.getDescription()).thenReturn("Object");
        when(it.getId()).thenReturn("test");

        new JUnitNotifier(runNotifier).pass(it);

        verify(runNotifier).fireTestFinished(Description.createSuiteDescription("Object", "test"));
    }

    @Test
    public void shouldDelegateToJunitRunNotifierForSkipped() {
        when(it.getDescription()).thenReturn("Object");
        when(it.getId()).thenReturn("test");

        new JUnitNotifier(runNotifier).skipped(it);

        verify(runNotifier).fireTestIgnored(Description.createSuiteDescription("Object", "test"));
    }

    @Test
    public void shouldDoNothingWhenNoTestsToRun() {
        new JUnitNotifier(runNotifier).finished();

        verifyNoMoreInteractions(runNotifier);
    }

    @Test
    public void shouldDelegateToRunNotifierForFailWithFailure() {
        when(it.getDescription()).thenReturn("Object");
        when(it.getId()).thenReturn("test");
        when(failure.getMessage()).thenReturn("expected 'foo' to be 'bar'");
        when(failure.getStack()).thenReturn("\tat foo.js:1\n\tat bar.js:1");
        new JUnitNotifier(runNotifier).fail(it, failure);

        ArgumentCaptor<Failure> captor = ArgumentCaptor.forClass(Failure.class);
        verify(runNotifier).fireTestFailure(captor.capture());
        Failure junitFailure = captor.getValue();

        assertThat(junitFailure.getDescription()).isEqualTo(Description.createSuiteDescription("Object", "test"));
        assertThat(junitFailure.getException()).isInstanceOf(RuntimeException.class);
        assertThat(junitFailure.getMessage()).isEqualTo("expected 'foo' to be 'bar'");
        StackTraceAsserts.assertThat(junitFailure.getException())
                .isEqualTo("expected 'foo' to be 'bar'\n\tat foo.js:1\n\tat bar.js:1");
    }
}
