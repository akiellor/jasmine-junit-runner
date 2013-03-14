package jasmine.junit;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JUnitNotifierTest {
    @Mock RunNotifier runNotifier;
    @Mock RhinoIt it;
    @Mock Exception exception;

    @Test
    public void shouldDelegateToJunitRunNotifierForStarted() {
        when(it.getStringDescription()).thenReturn("Object");
        when(it.getId()).thenReturn("test");

        new JUnitNotifier(runNotifier).started(it);

        verify(runNotifier).fireTestStarted(Description.createSuiteDescription("Object", "test"));
    }

    @Test
    public void shouldDelegateToJunitRunNotifierForPass() {
        when(it.getStringDescription()).thenReturn("Object");
        when(it.getId()).thenReturn("test");

        new JUnitNotifier(runNotifier).pass(it);

        verify(runNotifier).fireTestFinished(Description.createSuiteDescription("Object", "test"));
    }

    @Test
    public void shouldDelegateToJunitRunNotifierForSkipped() {
        when(it.getStringDescription()).thenReturn("Object");
        when(it.getId()).thenReturn("test");

        new JUnitNotifier(runNotifier).skipped(it);

        verify(runNotifier).fireTestIgnored(Description.createSuiteDescription("Object", "test"));
    }

    @Test
    public void shouldDelegateToRunNotifierForFail() {
        when(it.getStringDescription()).thenReturn("Object");
        when(it.getId()).thenReturn("test");
        when(it.getFirstFailedStacktrace()).thenReturn(exception);

        new JUnitNotifier(runNotifier).fail(it);

        ArgumentCaptor<Failure> captor = ArgumentCaptor.forClass(Failure.class);
        verify(runNotifier).fireTestFailure(captor.capture());
        Failure failure = captor.getValue();
        assertThat(failure.getDescription()).isEqualTo(Description.createSuiteDescription("Object", "test"));
        assertThat(failure.getException()).isEqualTo(exception);
    }

    @Test
    public void shouldThrowInitializationErrorWhenNoSpecsToRun() {
        try{
            new JUnitNotifier(runNotifier).nothingToRun();
            fail();
        }catch(RuntimeException e){
            assertThat(e.getMessage()).isEqualTo("No specs to run.");
        }
    }
}
