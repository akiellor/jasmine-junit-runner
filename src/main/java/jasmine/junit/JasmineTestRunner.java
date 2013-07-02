package jasmine.junit;

import jasmine.runtime.rhino.RhinoContext;
import jasmine.runtime.Backend;
import jasmine.runtime.Configuration;
import jasmine.runtime.Hooks;
import jasmine.runtime.rhino.RhinoBackend;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.tools.debugger.Main;

public class JasmineTestRunner extends Runner {
    private final TestObject test;
    private final Configuration configuration;
    private final Backend jasmine;

    public JasmineTestRunner(Class<?> testClass) {
        this.test = new TestObject(testClass);
        this.configuration = new AnnotationConfiguration(test.getAnnotation().or(DefaultSuite.getAnnotation()), test.getDefaultSpecPath());
        this.jasmine = new RhinoBackend(configuration);
    }

    protected JasmineTestRunner(Configuration configuration, TestObject test, RhinoBackend jasmine){
        this.configuration = configuration;
        this.test = test;
        this.jasmine = jasmine;
    }

    private Main createDebugger() {
        Main debugger = new Main("JS Debugger");

        debugger.setExitAction(new Runnable() {
            public void run() {
                System.exit(0);
            }
        });

        debugger.attachTo(ContextFactory.getGlobal());
        debugger.pack();
        debugger.setSize(600, 460);
        debugger.setVisible(true);

        return debugger;
    }

    @Override
    public Description getDescription() {
        Description description = test.getDescription();
        DescriptionBuilder descriptionBuilder = new DescriptionBuilder(description);
        jasmine.accept(descriptionBuilder);
        return description;
    }

    @Override
    public void run(final RunNotifier notifier) {
        if (configuration.debug()) {
            createDebugger().doBreak();
        }

        jasmine.execute(new Hooks(){
            @Override public void beforeAll(RhinoContext context) {
                test.befores(context);
            }

            @Override public void afterAll(RhinoContext context) {
                test.afters(context);
            }
        }, new JUnitNotifier(notifier));
    }
}
