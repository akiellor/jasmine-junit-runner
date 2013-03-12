package jasmine.runtime;

import jasmine.runtime.rhino.RhinoRuntime;
import org.junit.runner.Description;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Jasmine {
    private static final List<String> JASMINE_LIBRARY = Collections.unmodifiableList(Arrays.asList(
            "js/lib/jasmine-1.3.1/jasmine.js",
            "js/lib/jasmine.delegator_reporter.js"
    ));

    private final Backend backend;

    public Jasmine(Configuration configuration, Description rootDescription) {
        this.backend = new RhinoRuntime(configuration, rootDescription, JASMINE_LIBRARY);
    }

    protected Jasmine(Backend backend) {
        this.backend = backend;
    }

    public Description getDescription() {
        return backend.getRootDescription();
    }

    public void execute(Hooks hooks, final Notifier notifier) {
        hooks.beforeAll(backend);

        backend.execute(notifier);

        hooks.afterAll(backend);
    }
}
