package jasmine.junit;

import jasmine.runtime.Backend;
import jasmine.runtime.Configuration;
import jasmine.runtime.rhino.RhinoBackend;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

public class JasmineTestRunner extends Runner {
    private final TestObject test;
    private final Configuration configuration;
    private final Backend jasmine;

    public JasmineTestRunner(Class<?> testClass) {
        this.test = new TestObject(testClass);
        this.configuration = new AnnotationConfiguration(test.getAnnotation().or(DefaultSuite.getAnnotation()), test.getDefaultSpecPath());
        this.jasmine = new RhinoBackend(configuration);
    }

    protected JasmineTestRunner(Configuration configuration, TestObject test, RhinoBackend jasmine) {
        this.configuration = configuration;
        this.test = test;
        this.jasmine = jasmine;
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
        jasmine.execute(new JUnitNotifier(notifier));
    }
}
