package jasmine.runtime;

import jasmine.runtime.rhino.RhinoIt;

public enum Status {
    PASSED {
        @Override public void notify(Notifier notifier, RhinoIt it) {
            notifier.pass(it);
        }
    },
    FAILED {
        @Override public void notify(Notifier notifier, RhinoIt it) {
            notifier.fail(it);
        }
    },
    SKIPPED {
        @Override public void notify(Notifier notifier, RhinoIt it) {
            notifier.skipped(it);
        }
    };

    public abstract void notify(Notifier notifier, RhinoIt it);
}
