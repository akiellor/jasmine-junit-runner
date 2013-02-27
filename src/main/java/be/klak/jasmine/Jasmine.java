package be.klak.jasmine;

import be.klak.rhino.RhinoContext;
import be.klak.utils.Futures;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.junit.runner.Description;
import org.mozilla.javascript.NativeObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Jasmine {
    private static final List<String> JASMINE_LIBRARY = Collections.unmodifiableList(Arrays.asList(
            "js/lib/jasmine-1.0.2/jasmine.js",
            "js/lib/jasmine-1.0.2/jasmine.delegator_reporter.js"
    ));

    public static final List<String> ENV_JS_LIBRARY = Collections.unmodifiableList(Arrays.asList(
            "js/lib/env.rhino.1.2.js",
            "js/lib/env.utils.js"
    ));

    private final Configuration configuration;
    private final RhinoContext context;
    private final Runner runner;
    private final ExecutorService executor;


    public Jasmine(Configuration configuration, Description rootDescription) {
        this.configuration = configuration;
        this.context = setUpRhinoScope();
        this.executor = Executors.newFixedThreadPool(10);
        NativeObject baseSuites = (NativeObject) context.evalJS("jasmine.getEnv().currentRunner()");
        this.runner = new be.klak.jasmine.Runner(baseSuites, context, rootDescription);
    }

    private RhinoContext setUpRhinoScope() {
        RhinoContext context = new RhinoContext();

        List<String> resources = new ArrayList<String>();
        if (configuration.envJs()) {
            resources.addAll(ENV_JS_LIBRARY);
            resources.add(configuration.jsRootFile("envJsOptions.js"));
        } else {
            resources.add("js/lib/no-env.js");
        }
        resources.addAll(JASMINE_LIBRARY);
        resources.addAll(configuration.sources());
        resources.addAll(configuration.specs());

        context.loadFromVirtualFileSystem(resources);

        context.evalJS("jasmine.getEnv().addReporter(new jasmine.DelegatorJUnitReporter());");

        return context;
    }

    public Description getDescription() {
        return runner.getDescription();
    }

    public void execute(Hooks hooks, final Notifier notifier) {
        hooks.beforeAll(context);

        Futures.await(Collections2.transform(runner.getAllIts(), new Function<It, Future<It>>() {
            @Override public Future<It> apply(final It spec) {
                return executor.submit(new Callable<It>() {
                    @Override public It call() throws Exception {
                        RhinoContext fork = context.fork();

                        spec.bind(fork).execute(notifier);

                        return spec;
                    }
                });
            }
        }));

        hooks.afterAll(context);
    }
}
