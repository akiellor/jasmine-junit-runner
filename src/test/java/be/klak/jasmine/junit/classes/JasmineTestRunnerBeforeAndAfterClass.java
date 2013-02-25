package be.klak.jasmine.junit.classes;

import be.klak.jasmine.junit.JasmineSuite;
import be.klak.rhino.RhinoContext;
import org.junit.After;
import org.junit.Before;


@JasmineSuite(specs = { "emptySpec.js" })
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
