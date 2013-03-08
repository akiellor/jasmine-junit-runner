package jasmine.runtime;

import jasmine.rhino.RhinoContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.inOrder;

@RunWith(MockitoJUnitRunner.class)
public class JasmineTest {
    @Mock Configuration configuration;
    @Mock RhinoContext context;
    @Mock Runner runner;
    @Mock Hooks hooks;
    @Mock Notifier notifier;

    @Test
    public void shouldExecuteHooksAroundTheExecutionOfTests() {
        Jasmine jasmine = new Jasmine(configuration, context, runner);

        jasmine.execute(hooks, notifier);

        InOrder order = inOrder(hooks, runner);
        order.verify(hooks).beforeAll(context);
        order.verify(runner).execute(notifier);
        order.verify(hooks).afterAll(context);
    }
}
