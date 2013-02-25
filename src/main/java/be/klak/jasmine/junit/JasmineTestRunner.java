package be.klak.jasmine.junit;

import be.klak.jasmine.*;
import be.klak.jasmine.generator.JasmineSpecRunnerGenerator;
import be.klak.rhino.RhinoContext;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.tools.debugger.Main;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JasmineTestRunner extends Runner {

	private static final int SLEEP_TIME_MILISECONDS = 50;

    private static final List<String> JASMINE_LIBRARY = Collections.unmodifiableList(Arrays.asList(
        "js/lib/jasmine-1.0.2/jasmine.js",
        "js/lib/jasmine-1.0.2/jasmine.delegator_reporter.js"
    ));

    public static final List<String> ENV_JS_LIBRARY = Collections.unmodifiableList(Arrays.asList(
        "js/lib/env.rhino.1.2.js",
        "js/lib/env.utils.js"
    ));

    private JasmineDescriptions jasmineSuite;

	protected final RhinoContext rhinoContext;
	private final Class<?> testClass;
    private final AnnotationConfiguration configuration;

    @JasmineSuite
	private class DefaultSuite { }

	public JasmineTestRunner(Class<?> testClass) {
		this.testClass = testClass;
        this.configuration = new AnnotationConfiguration(getJasmineSuiteAnnotationFromTestClass(), StringUtils.uncapitalize(testClass.getSimpleName()).replace("Test", "Spec") + ".js");

        Main debugger = null;
        if (configuration.debug()) {
			debugger = createDebugger();
		}

		this.rhinoContext = setUpRhinoScope();

        if (configuration.debug()) {
			debugger.doBreak();
		}
	}

	private RhinoContext setUpRhinoScope() {
		RhinoContext context = new RhinoContext();

        pre(context);

        List<String> resources = new ArrayList<String>();
        if (configuration.envJs()) {
            resources.addAll(ENV_JS_LIBRARY);
            resources.add(configuration.jsRootFile("envJsOptions.js"));
        } else {
            resources.add(configuration.jsRootFile("lib/no-env.js"));
		}
        resources.addAll(JASMINE_LIBRARY);
        resources.addAll(configuration.sources());
        resources.addAll(configuration.specs());

        context.loadFromVirtualFileSystem(resources);

        context.evalJS("jasmine.getEnv().addReporter(new jasmine.DelegatorJUnitReporter());");

        return context;
	}

    protected void pre(RhinoContext context) { }

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

	private JasmineSuite getJasmineSuiteAnnotationFromTestClass() {
		JasmineSuite suiteAnnotation = testClass.getAnnotation(JasmineSuite.class);
		if (suiteAnnotation == null) {
			suiteAnnotation = DefaultSuite.class.getAnnotation(JasmineSuite.class);
		}
		return suiteAnnotation;
	}

	private void resetEnvjsWindowSpace() {
		URL blankUrl = Thread.currentThread().getContextClassLoader().getResource("js/lib/blank.html");

		if (blankUrl == null) {
			throw new IllegalStateException("Unable to load js/lib/blank.html from classpath");
		}

		String blankUrlStr = blankUrl.toExternalForm();

		// "file:/path/to/file" is not legal, but "file:///path/to/file" is
		if (blankUrlStr.startsWith("file:/") && (! blankUrlStr.startsWith("file:///"))) {
			blankUrlStr = "file://" + blankUrlStr.substring(5);
		}

		this.rhinoContext.evalJS(String.format(
			"window.location = '%s';",
			blankUrlStr
		));
	}

	private JasmineDescriptions getJasmineDescriptions() {
		if (this.jasmineSuite == null) {
			NativeArray baseSuites = (NativeArray) rhinoContext.evalJS("jasmine.getEnv().currentRunner().suites()");
			this.jasmineSuite = new JasmineJSSuiteConverter(rhinoContext).convertToJunitDescriptions(testClass, baseSuites);
		}
		return this.jasmineSuite;
	}

	@Override
	public Description getDescription() {
		return getJasmineDescriptions().getRootDescription();
	}

	@Override
	public void run(RunNotifier notifier) {
		generateSpecRunnerIfNeeded();

		for (It spec : getJasmineDescriptions().getSpecs()) {
			Object testClassInstance = createTestClassInstance();
			fireMethodsWithSpecifiedAnnotationIfAny(testClassInstance, Before.class);

			try {
				notifier.fireTestStarted(spec.getDescription());
				spec.execute(rhinoContext);
				while (!spec.isDone()) {
					waitALittle();
				}

				reportSpecResultToNotifier(notifier, spec);

                if (configuration.envJs()) {
					resetEnvjsWindowSpace();
				}
			} finally {
				fireMethodsWithSpecifiedAnnotationIfAny(testClassInstance, After.class);
			}
		}

        after();
	}

    protected void after() {
		this.rhinoContext.exit();
    }

	private Object createTestClassInstance() {
		try {
			return testClass.newInstance();
		} catch (Exception ex) {
			throw new RuntimeException("Unable to create a new instance of testClass " + testClass.getSimpleName()
					+ " using a no-arg constructor", ex);
		}
	}

	private void fireMethodsWithSpecifiedAnnotationIfAny(Object testClassInstance, Class<? extends Annotation> annotation) {
		for (Method method : testClass.getMethods()) {

			try {
				if (method.getAnnotation(annotation) != null) {
					method.setAccessible(true);
					Class<?>[] parameterTypes = method.getParameterTypes();
					if (parameterTypes.length == 0) {
						method.invoke(testClassInstance, (Object[]) null);
					} else if (parameterTypes.length == 1 && RhinoContext.class.isAssignableFrom(parameterTypes[0])) {
						method.invoke(testClassInstance, new Object[] { this.rhinoContext });
					} else {
						throw new IllegalStateException("Annotated method does not have zero or rhinoContext as parameterTypes");
					}
				}
			} catch (Exception ex) {
				throw new RuntimeException(
						"Exception while firing " + annotation.getSimpleName() + " method: " + method.getName(), ex);
			}
		}
	}

	private void generateSpecRunnerIfNeeded() {
        if (configuration.generateSpecRunner()) {
            new JasmineSpecRunnerGenerator(configuration, testClass.getSimpleName() + "Runner.html").generate();
		}
	}

	private void reportSpecResultToNotifier(RunNotifier notifier, It spec) {
		if (spec.isPassed(rhinoContext)) {
			notifier.fireTestFinished(spec.getDescription());
		} else if (spec.isFailed(rhinoContext)) {
			notifier.fireTestFailure(spec.getJunitFailure(rhinoContext));
		} else {
			throw new IllegalStateException("Unexpected spec status received: " + spec);
		}
	}

	private void waitALittle() {
		try {
			Thread.sleep(SLEEP_TIME_MILISECONDS);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}