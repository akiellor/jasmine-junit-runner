package jasmine.runtime.rhino;

import jasmine.runtime.Failure;
import jasmine.runtime.It;
import jasmine.runtime.Notifier;

abstract class Status {
    public abstract void notify(Notifier notifier);

    public static class Failed extends Status {
        private final It it;
        private final Failure failure;

        public Failed(It it, Failure failure) {
            this.it = it;
            this.failure = failure;
        }

        public void notify(Notifier notifier) {
            notifier.fail(it, failure);
        }
    }

    public static class Passed extends Status {
        private final It it;

        public Passed(It it) {
            this.it = it;
        }

        public void notify(Notifier notifier) {
            notifier.pass(it);
        }
    }

    public static class Skipped extends Status {
        private final It it;

        public Skipped(It it) {
            this.it = it;
        }

        public void notify(Notifier notifier) {
            notifier.skipped(it);
        }
    }
}
