package jasmine.junit;

import jasmine.runtime.Configuration;
import jasmine.runtime.Hooks;
import jasmine.runtime.Jasmine;
import jasmine.generator.JasmineSpecRunnerGenerator;
import jasmine.rhino.RhinoContext;
import org.apache.commons.lang.StringUtils;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.tools.debugger.Main;

public class JasmineTestRunner extends Runner {
    private final TestObject test;
    private final Configuration configuration;
    private final Jasmine jasmine;

    public JasmineTestRunner(Class<?> testClass) {
        this.test = new TestObject(testClass);
        this.configuration = new AnnotationConfiguration(test.getAnnotation().or(DefaultSuite.getAnnotation()), StringUtils.uncapitalize(test.getName()).replace("Test", "Spec") + ".js");
        this.jasmine = new Jasmine(configuration, Description.createSuiteDescription(testClass));
    }

    protected JasmineTestRunner(Configuration configuration, TestObject test, Jasmine jasmine){
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
        return jasmine.getDescription();
    }

    @Override
    public void run(final RunNotifier notifier) {
        if (configuration.debug()) {
            createDebugger().doBreak();
        }

        generateSpecRunnerIfNeeded();

        jasmine.execute(new Hooks(){
            @Override public void beforeAll(RhinoContext context) {
                test.befores(context);
            }

            @Override public void afterAll(RhinoContext context) {
                test.afters(context);
            }
        }, new JUnitNotifier(notifier));
    }

    private void generateSpecRunnerIfNeeded() {
        if (configuration.generateSpecRunner()) {
            new JasmineSpecRunnerGenerator(configuration, test.getName() + "Runner.html").generate();
        }
    }
}
