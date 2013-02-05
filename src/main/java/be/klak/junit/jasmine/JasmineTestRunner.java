package be.klak.junit.jasmine;

import be.klak.junit.resources.ClasspathResource;
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
import java.util.Arrays;
import java.util.List;

public class JasmineTestRunner extends Runner {

	private static final int SLEEP_TIME_MILISECONDS = 50;

    private static final List<ClasspathResource> jasmineLibrary = Arrays.asList(
        new ClasspathResource("js/lib/jasmine-1.0.2/jasmine.js"),
        new ClasspathResource("js/lib/jasmine-1.0.2/jasmine.delegator_reporter.js")
    );

    public static final List<ClasspathResource> ENV_JS_LIBRARY = Arrays.asList(
            new ClasspathResource("js/lib/env.rhino.1.2.js"),
            new ClasspathResource("js/lib/env.utils.js")
    );

    private JasmineDescriptions jasmineSuite;

	protected final RhinoContext rhinoContext;
	protected final JasmineSuite suiteAnnotation;
	private final Class<?> testClass;

	@JasmineSuite
	private class DefaultSuite { }

	public JasmineTestRunner(Class<?> testClass) {
		this.testClass = testClass;
		this.suiteAnnotation = getJasmineSuiteAnnotationFromTestClass();

		Main debugger = null;
		if (this.suiteAnnotation.debug()) {
			debugger = createDebugger();
		}

		this.rhinoContext = setUpRhinoScope();

		if (this.suiteAnnotation.debug()) {
			debugger.doBreak();
		}
	}

	private RhinoContext setUpRhinoScope() {
		RhinoContext context = new RhinoContext();

        pre(context);

        if (suiteAnnotation.envJs()) {
            context.load(ENV_JS_LIBRARY);
            context.load(suiteAnnotation.jsRootDir() + "/envJsOptions.js");
        } else {
			context.load(suiteAnnotation.jsRootDir(), "/lib/no-env.js");
		}

		setUpJasmine(context);

		context.load(suiteAnnotation.sourcesRootDir() + "/", suiteAnnotation.sources());
		context.load(suiteAnnotation.jsRootDir() + "/specs/", getJasmineSpecs(suiteAnnotation));

		return context;
	}

    protected void pre(RhinoContext context) { }

	private void setUpJasmine(RhinoContext context) {
        context.load(jasmineLibrary);
		context.evalJS("jasmine.getEnv().addReporter(new jasmine.DelegatorJUnitReporter());");
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

	private JasmineSuite getJasmineSuiteAnnotationFromTestClass() {
		JasmineSuite suiteAnnotation = testClass.getAnnotation(JasmineSuite.class);
		if (suiteAnnotation == null) {
			suiteAnnotation = DefaultSuite.class.getAnnotation(JasmineSuite.class);
		}
		return suiteAnnotation;
	}

	private String[] getJasmineSpecs(JasmineSuite suiteAnnotation) {
		if (suiteAnnotation.specs().length == 0) {
			return new String[] { StringUtils.uncapitalize(testClass.getSimpleName()).replace("Test", "Spec") + ".js" };
		}
		return suiteAnnotation.specs();
	}

	private void resetEnvjsWindowSpace() {
		URL blankUrl = Thread
			.currentThread()
			.getContextClassLoader()
			.getResource("js/lib/blank.html");

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

		for (JasmineSpec spec : getJasmineDescriptions().getSpecs()) {
			Object testClassInstance = createTestClassInstance();
			fireMethodsWithSpecifiedAnnotationIfAny(testClassInstance, Before.class);

			try {
				notifier.fireTestStarted(spec.getDescription());
				spec.execute(rhinoContext);
				while (!spec.isDone()) {
					waitALittle();
				}

				reportSpecResultToNotifier(notifier, spec);

				if (suiteAnnotation.envJs()) {
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
		if (suiteAnnotation.generateSpecRunner()) {
			String[] jasmineSpecs = getJasmineSpecs(suiteAnnotation);
			StringBuffer outputPath = new StringBuffer(suiteAnnotation.jsRootDir()).append("/runners");
			if (StringUtils.isNotBlank(suiteAnnotation.specRunnerSubDir())) {
			  outputPath.append('/').append(suiteAnnotation.specRunnerSubDir());
			}
			new JasmineSpecRunnerGenerator(jasmineSpecs, suiteAnnotation, outputPath.toString(),
					testClass.getSimpleName()
							+ "Runner.html")
					.generate();
		}
	}

	private void reportSpecResultToNotifier(RunNotifier notifier, JasmineSpec spec) {
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
