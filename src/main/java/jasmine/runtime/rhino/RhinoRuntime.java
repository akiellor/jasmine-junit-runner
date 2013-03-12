package jasmine.runtime.rhino;

import jasmine.rhino.RhinoContext;
import jasmine.runtime.Backend;
import jasmine.runtime.Configuration;
import jasmine.runtime.Notifier;
import org.junit.runner.Description;
import org.mozilla.javascript.NativeObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RhinoRuntime implements Backend {
    public static final List<String> ENV_JS_LIBRARY = Collections.unmodifiableList(Arrays.asList(
            "js/lib/env.rhino.1.2.js",
            "js/lib/env.utils.js"
    ));

    private final Runner runner;

    public RhinoRuntime(Configuration configuration, Description rootDescription, List<String> jasmineLibrary){
        RhinoContext context = new RhinoContext(configuration.getJavascriptPath());

        List<String> resources = new ArrayList<String>();
        if (configuration.envJs()) {
            resources.addAll(ENV_JS_LIBRARY);
            resources.add("envJsOptions.js");
        } else {
            resources.add("js/lib/no-env.js");
        }
        resources.addAll(jasmineLibrary);
        resources.addAll(configuration.sources());
        resources.addAll(configuration.specs());

        context.loadFromVirtualFileSystem(resources);

        context.evalJS("jasmine.getEnv().addReporter(new jasmine.DelegatorJUnitReporter());");

        NativeObject baseSuites = (NativeObject) context.evalJS("jasmine.getEnv().currentRunner()");

        this.runner = new Runner(baseSuites, context, rootDescription);
    }

    public Description getRootDescription() {
        return runner.getDescription();
    }

    @Override public void execute(Notifier notifier) {
        runner.execute(notifier);
    }
}
