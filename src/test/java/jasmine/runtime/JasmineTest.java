package jasmine.runtime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.inOrder;

@RunWith(MockitoJUnitRunner.class)
public class JasmineTest {
    @Mock Backend backend;
    @Mock Hooks hooks;
    @Mock Notifier notifier;

    @Test
    public void shouldExecuteHooksAroundTheExecutionOfTests() {
        Jasmine jasmine = new Jasmine(backend);

        jasmine.execute(hooks, notifier);

        InOrder order = inOrder(hooks, backend);
        order.verify(hooks).beforeAll(backend);
        order.verify(backend).execute(notifier);
        order.verify(hooks).afterAll(backend);
    }
}
