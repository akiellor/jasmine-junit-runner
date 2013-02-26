package be.klak.jasmine.junit;

import be.klak.jasmine.It;
import be.klak.jasmine.generator.JasmineSpecRunnerGenerator;
import be.klak.rhino.RhinoContext;
import be.klak.utils.Exceptions;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.tools.debugger.Main;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;

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

    private be.klak.jasmine.Runner jasmineSuite;

    protected final RhinoContext rhinoContext;
    private final Class<?> testClass;
    private final AnnotationConfiguration configuration;

    @JasmineSuite
    private class DefaultSuite {
    }

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

    protected void pre(RhinoContext context) {
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

    private void resetEnvjsWindowSpace() {
        URL blankUrl = Thread.currentThread().getContextClassLoader().getResource("js/lib/blank.html");

        if (blankUrl == null) {
            throw new IllegalStateException("Unable to load js/lib/blank.html from classpath");
        }

        String blankUrlStr = blankUrl.toExternalForm();

        // "file:/path/to/file" is not legal, but "file:///path/to/file" is
        if (blankUrlStr.startsWith("file:/") && (!blankUrlStr.startsWith("file:///"))) {
            blankUrlStr = "file://" + blankUrlStr.substring(5);
        }

        this.rhinoContext.evalJS(String.format(
                "window.location = '%s';",
                blankUrlStr
        ));
    }

    private be.klak.jasmine.Runner getJasmineDescriptions() {
        if (this.jasmineSuite == null) {
            NativeObject baseSuites = (NativeObject) rhinoContext.evalJS("jasmine.getEnv().currentRunner()");
            this.jasmineSuite = new be.klak.jasmine.Runner(baseSuites, rhinoContext, Description.createSuiteDescription(testClass));
        }
        return this.jasmineSuite;
    }

    @Override
    public Description getDescription() {
        return getJasmineDescriptions().getDescription();
    }

    @Override
    public void run(final RunNotifier notifier) {
        generateSpecRunnerIfNeeded();

        final ExecutorService executor = Executors.newFixedThreadPool(10);

        Object testClassInstance = createTestClassInstance();
        fireMethodsWithSpecifiedAnnotationIfAny(rhinoContext, testClassInstance, Before.class);

        Collection<Future<It>> results = Collections2.transform(getJasmineDescriptions().getAllIts(), new Function<It, Future<It>>() {
            @Override public Future<It> apply(final It spec) {
                return executor.submit(new Callable<It>() {
                    @Override public It call() throws Exception {
                        RhinoContext fork = rhinoContext.fork();

                        notifier.fireTestStarted(spec.getDescription());

                        spec.bind(fork).execute();

                        while(!spec.isDone()) { waitALittle(); }

                        if (configuration.envJs()) {
                            JasmineTestRunner.this.resetEnvjsWindowSpace();
                        }

                        return spec;
                    }
                });
            }
        });

        for (final Future<It> spec : results) {
            try {
                reportSpecResultToNotifier(notifier, spec.get());
            } catch (InterruptedException e) {
                throw Exceptions.unchecked(e);
            } catch (ExecutionException e) {
                throw Exceptions.unchecked(e);
            }
        }

        fireMethodsWithSpecifiedAnnotationIfAny(rhinoContext, testClassInstance, After.class);

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

    private void fireMethodsWithSpecifiedAnnotationIfAny(RhinoContext fork, Object testClassInstance, Class<? extends Annotation> annotation) {
        for (Method method : testClass.getMethods()) {

            try {
                if (method.getAnnotation(annotation) != null) {
                    method.setAccessible(true);
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length == 0) {
                        method.invoke(testClassInstance, (Object[]) null);
                    } else if (parameterTypes.length == 1 && RhinoContext.class.isAssignableFrom(parameterTypes[0])) {
                        method.invoke(testClassInstance, fork);
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
        if (spec.isPassed()) {
            notifier.fireTestFinished(spec.getDescription());
        } else if (spec.isFailed()) {
            notifier.fireTestFailure(spec.getJunitFailure());
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
