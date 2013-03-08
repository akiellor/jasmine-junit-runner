package jasmine.runtime.junit;


import jasmine.runtime.junit.classes.JasmineTestRunnerBeforeAndAfterClass;
import jasmine.runtime.junit.classes.JasmineTestRunnerBeforeAndAfterSuperClass;
import jasmine.runtime.rhino.RhinoContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class JasmineTestRunnerBeforeAndAfterTest {

    @Mock
    private RunNotifier notifierMock;

    @Test
    public void useJasmineRunnerOnJasmineTestRunnerBeforeAndAfterClass() throws NoSuchMethodException {
        JasmineTestRunnerBeforeAndAfterSuperClass.runs = new ArrayList<Method>();

        new JasmineTestRunner(JasmineTestRunnerBeforeAndAfterClass.class).run(notifierMock);

        assertThat(JasmineTestRunnerBeforeAndAfterSuperClass.runs).containsOnly(
                JasmineTestRunnerBeforeAndAfterClass.class.getMethod("runMij"),
                JasmineTestRunnerBeforeAndAfterSuperClass.class.getMethod("runMijOok", RhinoContext.class),
                JasmineTestRunnerBeforeAndAfterClass.class.getMethod("runMijAfter"),
                JasmineTestRunnerBeforeAndAfterClass.class.getMethod("runMijAfterOok", RhinoContext.class)
        );
    }
}
