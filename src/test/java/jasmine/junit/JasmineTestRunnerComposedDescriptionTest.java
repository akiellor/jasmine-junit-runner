package jasmine.junit;


import jasmine.junit.classes.JasmineTestRunnerComposedDescriptionSpec;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class JasmineTestRunnerComposedDescriptionTest {
    @Mock
    private RunNotifier notifierMock;

    @Test
    public void useJasmineRunnerOnJasmineTestRunnerComposedDescriptionClass() throws NoSuchMethodException {
        new JasmineTestRunner(JasmineTestRunnerComposedDescriptionSpec.class).run(notifierMock);
    }
}
