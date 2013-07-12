package jasmine.runtime.rhino;

import jasmine.runtime.Backend;
import jasmine.runtime.Configuration;
import jasmine.runtime.JasmineVisitor;
import jasmine.runtime.Notifier;
import org.mozilla.javascript.NativeObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RhinoBackend implements Backend {
    private static final List<String> JASMINE_LIBRARY = Collections.unmodifiableList(Arrays.asList(
            "js/lib/jasmine-1.3.1/jasmine.js",
            "js/lib/jasmine.delegator_reporter.js",
            "js/lib/jasmine.expectation_result.js"
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

    protected RhinoBackend(Configuration configuration, RhinoContext context, RhinoRunner runner) {
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

    public void accept(JasmineVisitor visitor) {
        runner.accept(visitor);
    }

    public void execute(final Notifier notifier) {
        runner.execute(notifier);
    }
}
