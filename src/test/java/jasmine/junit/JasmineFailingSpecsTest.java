package jasmine.junit;

import jasmine.StackTraceAsserts;
import jasmine.junit.classes.JasmineTestRunnerExceptionInJSCode;
import jasmine.junit.classes.JasmineTestRunnerExceptionInSpec;
import jasmine.junit.classes.JasmineTestRunnerFailingSpec;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mozilla.javascript.EvaluatorException;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class JasmineFailingSpecsTest {

    @Mock
    private RunNotifier notifierMock;

    @Test
    public void shouldNotifyOfSingleFailure() {
        new Jasmine(JasmineTestRunnerFailingSpec.class).run(notifierMock);

        ArgumentCaptor<Failure> failureCaptor = ArgumentCaptor.forClass(Failure.class);
        ArgumentCaptor<Description> descriptionCaptor = ArgumentCaptor.forClass(Description.class);
        verify(notifierMock).fireTestStarted(descriptionCaptor.capture());
        verify(notifierMock).fireTestFailure(failureCaptor.capture());
        verifyNoMoreInteractions(notifierMock);

        Failure failure = failureCaptor.getValue();
        Description startedDescription = descriptionCaptor.getValue();

        assertThat(failure.getDescription()).isEqualTo(startedDescription);
        assertThat(failure.getDescription().getDisplayName()).isEqualTo("will always fail");
        assertThat(failure.getMessage()).isEqualTo("Expected true to be false.");
        StackTraceAsserts.assertThat(failure.getException())
                .contains("failingSpec.js:3");
    }

    @Test
    public void shouldNotifyOfSingleExceptionWithinSpecFunction() {
        new Jasmine(JasmineTestRunnerExceptionInSpec.class).run(notifierMock);

        ArgumentCaptor<Failure> failureCaptor = ArgumentCaptor.forClass(Failure.class);
        ArgumentCaptor<Description> descriptionCaptor = ArgumentCaptor.forClass(Description.class);
        verify(notifierMock).fireTestStarted(descriptionCaptor.capture());
        verify(notifierMock).fireTestFailure(failureCaptor.capture());
        verifyNoMoreInteractions(notifierMock);

        Failure failure = failureCaptor.getValue();
        Description startedDescription = descriptionCaptor.getValue();

        assertThat(failure.getDescription()).isEqualTo(startedDescription);
        assertThat(failure.getDescription().getDisplayName()).isEqualTo("will always crash");
        assertThat(failure.getMessage()).contains("ReferenceError: \"OEIWANU\"");
        StackTraceAsserts.assertThat(failure.getException())
                .contains("crashingSpec.js:3");
    }

    @Test(expected = EvaluatorException.class)
    public void shouldCrashWhileTryingToLoadFaultyJSSpecFile() {
        new Jasmine(JasmineTestRunnerExceptionInJSCode.class);
    }
}
