package jasmine.junit.classes;

import jasmine.junit.JasmineSuite;
import jasmine.runtime.rhino.RhinoContext;
import org.junit.After;
import org.junit.Before;


@JasmineSuite(specs = { "specs/emptySpec.js" })
public class JasmineTestRunnerBeforeAndAfterClass extends JasmineTestRunnerBeforeAndAfterSuperClass {

    @Before
    public void runMij() throws NoSuchMethodException {
        runs.add(this.getClass().getMethod("runMij"));
    }

    @After
    public void runMijAfter() throws NoSuchMethodException {
        runs.add(this.getClass().getMethod("runMijAfter"));
    }

    @After
    public void runMijAfterOok(RhinoContext context) throws NoSuchMethodException {
        runs.add(this.getClass().getMethod("runMijAfterOok", RhinoContext.class));
    }

    public void runMijNiet() throws NoSuchMethodException {
        runs.add(this.getClass().getMethod("runMijNiet", RhinoContext.class));
    }
}
