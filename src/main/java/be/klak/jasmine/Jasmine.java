package be.klak.jasmine;

import be.klak.rhino.RhinoContext;
import org.junit.runner.Description;
import org.mozilla.javascript.NativeObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Jasmine {
    private static final List<String> JASMINE_LIBRARY = Collections.unmodifiableList(Arrays.asList(
            "js/lib/jasmine-1.3.1/jasmine.js",
            "js/lib/jasmine.delegator_reporter.js"
    ));

    public static final List<String> ENV_JS_LIBRARY = Collections.unmodifiableList(Arrays.asList(
            "js/lib/env.rhino.1.2.js",
            "js/lib/env.utils.js"
    ));

    private final Configuration configuration;
    private final RhinoContext context;
    private final Runner runner;


    public Jasmine(Configuration configuration, Description rootDescription) {
        this.configuration = configuration;
        this.context = setUpRhinoScope();
        NativeObject baseSuites = (NativeObject) context.evalJS("jasmine.getEnv().currentRunner()");
        this.runner = new Runner(baseSuites, context, rootDescription);
    }

    protected Jasmine(Configuration configuration, RhinoContext context, Runner runner){
        this.configuration = configuration;
        this.context = context;
        this.runner = runner;
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

        runner.execute(notifier);

        hooks.afterAll(context);
    }
}
