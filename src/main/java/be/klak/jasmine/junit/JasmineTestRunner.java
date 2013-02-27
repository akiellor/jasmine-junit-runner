package be.klak.jasmine.junit;

import be.klak.jasmine.It;
import be.klak.jasmine.generator.JasmineSpecRunnerGenerator;
import be.klak.rhino.RhinoContext;
import be.klak.utils.Exceptions;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.apache.commons.lang.StringUtils;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.tools.debugger.Main;

import java.util.*;
import java.util.concurrent.*;

public class JasmineTestRunner extends Runner {

    private static final List<String> JASMINE_LIBRARY = Collections.unmodifiableList(Arrays.asList(
            "js/lib/jasmine-1.0.2/jasmine.js",
            "js/lib/jasmine-1.0.2/jasmine.delegator_reporter.js"
    ));

    public static final List<String> ENV_JS_LIBRARY = Collections.unmodifiableList(Arrays.asList(
            "js/lib/env.rhino.1.2.js",
            "js/lib/env.utils.js"
    ));

    private be.klak.jasmine.Runner runner;

    protected final RhinoContext rhinoContext;
    private final AnnotationConfiguration configuration;
    private final TestObject test;
    private final ExecutorService executor;

    public JasmineTestRunner(Class<?> testClass) {
        this.test = new TestObject(testClass);
        this.configuration = new AnnotationConfiguration(test.getAnnotation().or(DefaultSuite.getAnnotation()), StringUtils.uncapitalize(test.getName()).replace("Test", "Spec") + ".js");
        this.executor = Executors.newFixedThreadPool(10);


        Main debugger = null;
        if (configuration.debug()) {
            debugger = createDebugger();
        }

        this.rhinoContext = setUpRhinoScope();

        if (configuration.debug()) {
            debugger.doBreak();
        }

        NativeObject baseSuites = (NativeObject) rhinoContext.evalJS("jasmine.getEnv().currentRunner()");
        this.runner = new be.klak.jasmine.Runner(baseSuites, rhinoContext, Description.createSuiteDescription(testClass));
    }

    private RhinoContext setUpRhinoScope() {
        RhinoContext context = new RhinoContext();

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
        return runner.getDescription();
    }

    @Override
    public void run(final RunNotifier notifier) {
        generateSpecRunnerIfNeeded();

        test.befores(rhinoContext);

        Collection<Future<It>> results = Collections2.transform(runner.getAllIts(), new Function<It, Future<It>>() {
            @Override public Future<It> apply(final It spec) {
                return executor.submit(new Callable<It>() {
                    @Override public It call() throws Exception {
                        RhinoContext fork = rhinoContext.fork();

                        notifier.fireTestStarted(spec.getDescription());

                        spec.bind(fork).execute();

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

        test.afters(rhinoContext);

        this.rhinoContext.exit();
    }

    private void generateSpecRunnerIfNeeded() {
        if (configuration.generateSpecRunner()) {
            new JasmineSpecRunnerGenerator(configuration, test.getName() + "Runner.html").generate();
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
}
