package jasmine.runtime.rhino;

import jasmine.runtime.Configuration;
import jasmine.runtime.Hooks;
import jasmine.runtime.Notifier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.inOrder;

@RunWith(MockitoJUnitRunner.class)
public class RhinoBackendTest {
    @Mock Configuration configuration;
    @Mock RhinoContext context;
    @Mock RhinoRunner runner;
    @Mock Hooks hooks;
    @Mock Notifier notifier;

    @Test
    public void shouldExecuteHooksAroundTheExecutionOfTests() {
        RhinoBackend jasmine = new RhinoBackend(configuration, context, runner);

        jasmine.execute(hooks, notifier);

        InOrder order = inOrder(hooks, runner);
        order.verify(hooks).beforeAll(context);
        order.verify(runner).execute(notifier);
        order.verify(hooks).afterAll(context);
    }
}
