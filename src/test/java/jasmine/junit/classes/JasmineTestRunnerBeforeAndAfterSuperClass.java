package jasmine.junit.classes;

import jasmine.rhino.RhinoContext;
import org.junit.Before;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class JasmineTestRunnerBeforeAndAfterSuperClass {
    public static List<Method> runs = new ArrayList<Method>();

    @Before
    public void runMijOok(RhinoContext context) throws NoSuchMethodException {
        runs.add(this.getClass().getMethod("runMijOok", RhinoContext.class));
    }
}