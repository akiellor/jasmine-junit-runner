package jasmine.junit;

import jasmine.runtime.Configuration;
import jasmine.runtime.Hooks;
import jasmine.runtime.rhino.RhinoBackend;
import jasmine.runtime.Notifier;
import jasmine.rhino.RhinoContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class JasmineTestRunnerTest {
    @Mock RhinoContext context;
    @Mock Configuration configuration;
    @Mock TestObject test;
    @Mock RhinoBackend jasmine;
    @Mock RunNotifier runNotifier;

    @Test
    public void shouldRunBeforesOnJasmineBeforeHook() {
        doAnswer(new Answer<Object>() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                ((Hooks)invocation.getArguments()[0]).beforeAll(context);
                return null;
            }
        }).when(jasmine).execute(Mockito.any(Hooks.class), Mockito.any(Notifier.class));

        JasmineTestRunner runner = new JasmineTestRunner(configuration, test, jasmine);

        runner.run(runNotifier);

        verify(test).befores(context);
    }

    @Test
    public void shouldRunAftersOnJasmineAfterHook() {
        doAnswer(new Answer<Object>() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                ((Hooks)invocation.getArguments()[0]).afterAll(context);
                return null;
            }
        }).when(jasmine).execute(Mockito.any(Hooks.class), Mockito.any(Notifier.class));

        JasmineTestRunner runner = new JasmineTestRunner(configuration, test, jasmine);

        runner.run(runNotifier);

        verify(test).afters(context);
    }

}
