package jasmine.junit;

import jasmine.runtime.It;
import jasmine.runtime.Notifier;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

public class JUnitNotifier implements Notifier {
    private final RunNotifier runNotifier;

    public JUnitNotifier(RunNotifier runNotifier) {
        this.runNotifier = runNotifier;
    }

    @Override public void pass(It rhinoIt) {
        runNotifier.fireTestFinished(rhinoIt.getDescription());
    }

    @Override public void fail(It rhinoIt) {
        runNotifier.fireTestFailure(new Failure(rhinoIt.getDescription(), new RuntimeException()));
    }

    @Override public void skipped(It rhinoIt) {
        runNotifier.fireTestIgnored(rhinoIt.getDescription());
    }

    @Override public void started(It rhinoIt) {
        runNotifier.fireTestStarted(rhinoIt.getDescription());
    }

    @Override public void nothingToRun() {
        throw new RuntimeException("No specs to run.");
    }
}
