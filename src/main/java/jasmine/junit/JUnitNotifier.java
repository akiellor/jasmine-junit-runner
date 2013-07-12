package jasmine.junit;

import jasmine.runtime.It;
import jasmine.runtime.Notifier;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

class JUnitNotifier implements Notifier {
    private final RunNotifier runNotifier;

    public JUnitNotifier(RunNotifier runNotifier) {
        this.runNotifier = runNotifier;
    }

    @Override public void pass(It it) {
        runNotifier.fireTestFinished(toDescription(it));
    }

    @Override
    public void fail(It it, jasmine.runtime.Failure failure) {
        runNotifier.fireTestFailure(new Failure(toDescription(it), failure.getThrowable()));
    }

    @Override public void skipped(It it) {
        runNotifier.fireTestIgnored(toDescription(it));
    }

    @Override public void started(It it) {
        runNotifier.fireTestStarted(toDescription(it));
    }

    @Override public void finished() {
    }

    private Description toDescription(It it) {
        return Description.createSuiteDescription(it.getDescription(), it.getId());
    }
}
