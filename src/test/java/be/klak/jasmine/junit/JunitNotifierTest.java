package be.klak.jasmine.junit;

import be.klak.jasmine.It;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JUnitNotifierTest {
    @Mock RunNotifier runNotifier;
    @Mock It it;
    @Mock Exception exception;

    @Test
    public void shouldDelegateToJunitRunNotifierForStarted() {
        when(it.getDescription()).thenReturn(Description.createTestDescription("Object", "test"));

        new JUnitNotifier(runNotifier).started(it);

        verify(runNotifier).fireTestStarted(Description.createTestDescription("Object", "test"));
    }

    @Test
    public void shouldDelegateToJunitRunNotifierForPass() {
        when(it.getDescription()).thenReturn(Description.createTestDescription("Object", "test"));

        new JUnitNotifier(runNotifier).pass(it);

        verify(runNotifier).fireTestFinished(Description.createTestDescription("Object", "test"));
    }

    @Test
    public void shouldDelegateToJunitRunNotifierForSkipped() {
        when(it.getDescription()).thenReturn(Description.createTestDescription("Object", "test"));

        new JUnitNotifier(runNotifier).skipped(it);

        verify(runNotifier).fireTestIgnored(Description.createTestDescription("Object", "test"));
    }

    @Test
    public void shouldDelegateToRunNotifierForFail() {
        when(it.getDescription()).thenReturn(Description.createTestDescription("Object", "test"));
        when(it.getFirstFailedStacktrace()).thenReturn(exception);

        new JUnitNotifier(runNotifier).fail(it);

        ArgumentCaptor<Failure> captor = ArgumentCaptor.forClass(Failure.class);
        verify(runNotifier).fireTestFailure(captor.capture());
        Failure failure = captor.getValue();
        assertThat(failure.getDescription()).isEqualTo(Description.createTestDescription("Object", "test"));
        assertThat(failure.getException()).isEqualTo(exception);
    }
}
