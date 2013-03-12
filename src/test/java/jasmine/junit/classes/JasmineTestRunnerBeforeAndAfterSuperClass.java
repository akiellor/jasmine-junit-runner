package jasmine.junit.classes;

import jasmine.runtime.Backend;
import org.junit.Before;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class JasmineTestRunnerBeforeAndAfterSuperClass {
    public static List<Method> runs = new ArrayList<Method>();

    @Before
    public void runMijOok(Backend backend) throws NoSuchMethodException {
        runs.add(this.getClass().getMethod("runMijOok", Backend.class));
    }
}
