package jasmine.junit;


import jasmine.junit.classes.JasmineTestRunnerBeforeAndAfterClass;
import jasmine.junit.classes.JasmineTestRunnerBeforeAndAfterSuperClass;
import jasmine.runtime.Backend;
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
                JasmineTestRunnerBeforeAndAfterSuperClass.class.getMethod("runMijOok", Backend.class),
                JasmineTestRunnerBeforeAndAfterClass.class.getMethod("runMijAfter"),
                JasmineTestRunnerBeforeAndAfterClass.class.getMethod("runMijAfterOok", Backend.class)
        );
    }
}
