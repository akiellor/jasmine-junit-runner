package be.klak.jasmine.junit;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import be.klak.jasmine.junit.JasmineTestRunner;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import be.klak.jasmine.junit.classes.JasmineTestRunnerSuccessSpec;
import be.klak.jasmine.junit.classes.JasmineTestRunnerDoesNotLoadEnvJS;

@RunWith(MockitoJUnitRunner.class)
public class JasmineFinishedSpecsTest {

    @Mock
    private RunNotifier notifierMock;

    @Test
    public void shouldNotifyOfSingleSuccess() {
        new JasmineTestRunner(JasmineTestRunnerSuccessSpec.class).run(notifierMock);

        ArgumentCaptor<Description> descriptionStartedCaptor = ArgumentCaptor.forClass(Description.class);
        ArgumentCaptor<Description> descriptionFinishedCaptor = ArgumentCaptor.forClass(Description.class);
        verify(notifierMock).fireTestStarted(descriptionStartedCaptor.capture());
        verify(notifierMock).fireTestFinished(descriptionFinishedCaptor.capture());
        verifyNoMoreInteractions(notifierMock);

        Description startedDescription = descriptionStartedCaptor.getValue();
        Description finishedDescription = descriptionFinishedCaptor.getValue();

        assertThat(startedDescription).isSameAs(finishedDescription);
        assertThat(startedDescription.getDisplayName()).isEqualTo("will always run");
    }

    @Test
    public void doesNotLoadEnvJsWhenSoConfigured() {
        new JasmineTestRunner(JasmineTestRunnerDoesNotLoadEnvJS.class).run(notifierMock);

        ArgumentCaptor<Description> descriptionStartedCaptor = ArgumentCaptor.forClass(Description.class);
        ArgumentCaptor<Description> descriptionFinishedCaptor = ArgumentCaptor.forClass(Description.class);
        verify(notifierMock).fireTestStarted(descriptionStartedCaptor.capture());
        verify(notifierMock).fireTestFinished(descriptionFinishedCaptor.capture());
        verifyNoMoreInteractions(notifierMock);

        Description startedDescription = descriptionStartedCaptor.getValue();
        Description finishedDescription = descriptionFinishedCaptor.getValue();

        assertThat(startedDescription).isSameAs(finishedDescription);
        assertThat(startedDescription.getDisplayName()).isEqualTo("is not loaded");
    }

}
