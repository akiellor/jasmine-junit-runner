package jasmine.runtime.rhino;

import jasmine.runtime.*;
import org.mozilla.javascript.NativeObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RhinoBackend implements Backend {
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
    private final RhinoRunner runner;


    public RhinoBackend(Configuration configuration) {
        this.configuration = configuration;
        this.context = setUpRhinoScope();
        NativeObject baseSuites = (NativeObject) context.evalJS("jasmine.getEnv().currentRunner()");
        this.runner = new RhinoRunner(baseSuites, context);
    }

    protected RhinoBackend(Configuration configuration, RhinoContext context, RhinoRunner runner){
        this.configuration = configuration;
        this.context = context;
        this.runner = runner;
    }

    private RhinoContext setUpRhinoScope() {
        RhinoContext context = new RhinoContext(configuration.getJavascriptPath());

        List<String> resources = new ArrayList<String>();
        resources.add("js/lib/no-env.js");
        resources.addAll(JASMINE_LIBRARY);
        resources.addAll(configuration.sources());
        resources.addAll(configuration.specs());

        context.loadFromVirtualFileSystem(resources);

        context.evalJS("jasmine.getEnv().addReporter(new jasmine.DelegatorJUnitReporter());");

        return context;
    }

    public void accept(JasmineVisitor visitor){
        runner.accept(visitor);
    }

    public void execute(Hooks hooks, final Notifier notifier) {
        hooks.beforeAll(context);

        runner.execute(notifier);

        hooks.afterAll(context);
    }
}
