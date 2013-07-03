package jasmine.cli;

import jasmine.runtime.Configuration;
import jasmine.runtime.Hooks;
import jasmine.runtime.It;
import jasmine.runtime.Notifier;
import jasmine.runtime.rhino.RhinoBackend;
import jasmine.runtime.rhino.RhinoContext;

public class Main {
    public static void main(String... args) {
        new Main(new CliConfiguration(args)).run();
    }

    private final RhinoBackend jasmine;

    public Main(Configuration configuration) {
        this.jasmine = new RhinoBackend(configuration);
    }

    public void run() {
        jasmine.execute(new CliHooks(), new CliNotifier());
    }

    private static class CliHooks implements Hooks {
        @Override
        public void beforeAll(RhinoContext context) {
        }

        @Override
        public void afterAll(RhinoContext context) {
        }
    }

    private static class CliNotifier implements Notifier {
        @Override
        public void pass(It it) {
            System.out.print(".");
        }

        @Override
        public void fail(It it, Throwable error) {
            System.out.print("F");
        }

        @Override
        public void skipped(It it) {
            System.out.print("-");
        }

        @Override
        public void started(It it) {

        }

        @Override
        public void nothingToRun() {
            System.out.println("No tests.");
        }
    }
}
