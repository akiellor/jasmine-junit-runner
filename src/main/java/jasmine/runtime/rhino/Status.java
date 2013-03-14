package jasmine.runtime.rhino;

import jasmine.runtime.It;
import jasmine.runtime.Notifier;

public abstract class Status {
    public abstract void notify(Notifier notifier);

    public static class Failed extends Status{
        private final It it;
        private final Throwable error;

        public Failed(It it, Throwable error) {
            this.it = it;
            this.error = error;
        }

        public void notify(Notifier notifier) {
            notifier.fail(it, error);
        }
    }

    public static class Passed extends Status{
        private final It it;

        public Passed(It it) {
            this.it = it;
        }

        public void notify(Notifier notifier) {
            notifier.pass(it);
        }
    }

    public static class Skipped extends Status{
        private final It it;

        public Skipped(It it) {
            this.it = it;
        }

        public void notify(Notifier notifier) {
            notifier.skipped(it);
        }
    }
}
