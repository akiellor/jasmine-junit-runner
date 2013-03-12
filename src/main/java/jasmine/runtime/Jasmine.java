package jasmine.runtime;

import org.junit.runner.Description;

public class Jasmine {
    private final Backend backend;

    public Jasmine(Backend backend) {
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
