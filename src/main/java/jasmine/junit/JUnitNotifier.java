package jasmine.junit;

import jasmine.runtime.It;
import jasmine.runtime.Notifier;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import java.io.PrintStream;
import java.io.PrintWriter;

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
        runNotifier.fireTestFailure(new Failure(toDescription(it), new JUnitJasmineException(failure)));
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

    private static class JUnitJasmineException extends RuntimeException{
        private final jasmine.runtime.Failure failure;

        public JUnitJasmineException(jasmine.runtime.Failure failure) {
            this.failure = failure;
        }

        @Override
        public String getMessage(){
            return failure.getMessage();
        }

        @Override
        public void printStackTrace() {
            printStackTrace(System.err);
        }

        @Override
        public void printStackTrace(PrintStream s) {
            printStackTrace(new PrintWriter(s));
        }

        @Override
        public void printStackTrace(PrintWriter s) {
            s.append(failure.getMessage());
            s.append("\n");
            s.append(failure.getStack());
            s.flush();
        }
    }
}
