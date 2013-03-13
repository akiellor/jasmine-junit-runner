package jasmine.runtime.rhino;

import jasmine.runtime.Notifier;

public enum Status {
    PASSED {
        @Override void notify(Notifier notifier, RhinoIt rhinoIt) {
            notifier.pass(rhinoIt);
        }
    },
    FAILED {
        @Override void notify(Notifier notifier, RhinoIt rhinoIt) {
            notifier.fail(rhinoIt);
        }
    },
    SKIPPED {
        @Override void notify(Notifier notifier, RhinoIt rhinoIt) {
            notifier.skipped(rhinoIt);
        }
    };

    abstract void notify(Notifier notifier, RhinoIt rhinoIt);
}
