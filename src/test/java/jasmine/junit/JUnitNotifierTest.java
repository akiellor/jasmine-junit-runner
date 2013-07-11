package jasmine.junit;

import jasmine.runtime.JasmineException;
import jasmine.runtime.rhino.RhinoIt;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.Fail.fail;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JUnitNotifierTest {
    @Mock RunNotifier runNotifier;
    @Mock RhinoIt it;
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
    public void shouldDelegateToRunNotifierForFail() {
        when(it.getDescription()).thenReturn("Object");
        when(it.getId()).thenReturn("test");
        when(it.getFirstFailedStacktrace()).thenReturn(exception);

        new JUnitNotifier(runNotifier).fail(it, it.getFirstFailedStacktrace());

        ArgumentCaptor<Failure> captor = ArgumentCaptor.forClass(Failure.class);
        verify(runNotifier).fireTestFailure(captor.capture());
        Failure failure = captor.getValue();
        assertThat(failure.getDescription()).isEqualTo(Description.createSuiteDescription("Object", "test"));
        assertThat(failure.getException()).isEqualTo(exception);
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
        when(failure.getThrowable()).thenReturn(new JasmineException());
        new JUnitNotifier(runNotifier).fail(it, failure);

        ArgumentCaptor<Failure> captor = ArgumentCaptor.forClass(Failure.class);
        verify(runNotifier).fireTestFailure(captor.capture());
        Failure junitFailure = captor.getValue();

        assertThat(junitFailure.getDescription()).isEqualTo(Description.createSuiteDescription("Object", "test"));
        assertThat(junitFailure.getException()).isInstanceOf(JasmineException.class);
    }
}
