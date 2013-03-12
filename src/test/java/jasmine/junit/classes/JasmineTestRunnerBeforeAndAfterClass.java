package jasmine.junit.classes;

import jasmine.junit.JasmineSuite;
import jasmine.runtime.Backend;
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
    public void runMijAfterOok(Backend backend) throws NoSuchMethodException {
        runs.add(this.getClass().getMethod("runMijAfterOok", Backend.class));
    }

    public void runMijNiet() throws NoSuchMethodException {
        runs.add(this.getClass().getMethod("runMijNiet"));
    }
}
