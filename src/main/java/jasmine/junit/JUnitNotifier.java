package jasmine.junit;

import jasmine.runtime.rhino.It;
import jasmine.runtime.Notifier;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

public class JUnitNotifier implements Notifier {
    private final RunNotifier runNotifier;

    public JUnitNotifier(RunNotifier runNotifier) {
        this.runNotifier = runNotifier;
    }

    @Override public void pass(It it) {
        runNotifier.fireTestFinished(it.getDescription());
    }

    @Override public void fail(It it) {
        runNotifier.fireTestFailure(new Failure(it.getDescription(), it.getFirstFailedStacktrace()));
    }

    @Override public void skipped(It it) {
        runNotifier.fireTestIgnored(it.getDescription());
    }

    @Override public void started(It it) {
        runNotifier.fireTestStarted(it.getDescription());
    }

    @Override public void nothingToRun() {
        throw new RuntimeException("No specs to run.");
    }
}
