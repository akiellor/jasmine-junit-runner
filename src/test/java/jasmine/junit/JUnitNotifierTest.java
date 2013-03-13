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
    @Mock RhinoIt rhinoIt;
    @Mock Exception exception;

    @Test
    public void shouldDelegateToJunitRunNotifierForStarted() {
        when(rhinoIt.getDescription()).thenReturn(Description.createTestDescription("Object", "test"));

        new JUnitNotifier(runNotifier).started(rhinoIt);

        verify(runNotifier).fireTestStarted(Description.createTestDescription("Object", "test"));
    }

    @Test
    public void shouldDelegateToJunitRunNotifierForPass() {
        when(rhinoIt.getDescription()).thenReturn(Description.createTestDescription("Object", "test"));

        new JUnitNotifier(runNotifier).pass(rhinoIt);

        verify(runNotifier).fireTestFinished(Description.createTestDescription("Object", "test"));
    }

    @Test
    public void shouldDelegateToJunitRunNotifierForSkipped() {
        when(rhinoIt.getDescription()).thenReturn(Description.createTestDescription("Object", "test"));

        new JUnitNotifier(runNotifier).skipped(rhinoIt);

        verify(runNotifier).fireTestIgnored(Description.createTestDescription("Object", "test"));
    }

    @Test
    public void shouldDelegateToRunNotifierForFail() {
        when(rhinoIt.getDescription()).thenReturn(Description.createTestDescription("Object", "test"));
        when(rhinoIt.getFirstFailedStacktrace()).thenReturn(exception);

        new JUnitNotifier(runNotifier).fail(rhinoIt);

        ArgumentCaptor<Failure> captor = ArgumentCaptor.forClass(Failure.class);
        verify(runNotifier).fireTestFailure(captor.capture());
        Failure failure = captor.getValue();
        assertThat(failure.getDescription()).isEqualTo(Description.createTestDescription("Object", "test"));
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
