package jasmine.junit;

import jasmine.runtime.Notifier;
import jasmine.runtime.rhino.RhinoIt;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

public class JUnitNotifier implements Notifier {
    private final RunNotifier runNotifier;

    public JUnitNotifier(RunNotifier runNotifier) {
        this.runNotifier = runNotifier;
    }

    @Override public void pass(RhinoIt it) {
        runNotifier.fireTestFinished(toDescription(it));
    }

    @Override public void fail(RhinoIt it) {
        runNotifier.fireTestFailure(new Failure(toDescription(it), it.getFirstFailedStacktrace()));
    }

    @Override public void skipped(RhinoIt it) {
        runNotifier.fireTestIgnored(toDescription(it));
    }

    @Override public void started(RhinoIt it) {
        runNotifier.fireTestStarted(toDescription(it));
    }

    @Override public void nothingToRun() {
        throw new RuntimeException("No specs to run.");
    }

    private Description toDescription(RhinoIt it) {
        return Description.createSuiteDescription(it.getStringDescription(), it.getId());
    }
}
