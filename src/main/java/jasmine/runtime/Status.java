package jasmine.runtime;

public enum Status {
    PASSED {
        @Override void notify(Notifier notifier, It it) {
            notifier.pass(it);
        }
    },
    FAILED {
        @Override void notify(Notifier notifier, It it) {
            notifier.fail(it);
        }
    },
    SKIPPED {
        @Override void notify(Notifier notifier, It it) {
            notifier.skipped(it);
        }
    };

    abstract void notify(Notifier notifier, It it);
}
