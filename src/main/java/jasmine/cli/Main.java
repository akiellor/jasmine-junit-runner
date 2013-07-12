package jasmine.cli;

import jasmine.runtime.Configuration;
import jasmine.runtime.Failure;
import jasmine.runtime.It;
import jasmine.runtime.Notifier;
import jasmine.runtime.rhino.RhinoBackend;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main {
    public static void main(String... args) {
        new Main(new CliConfiguration(args)).run();
    }

    private final RhinoBackend jasmine;

    public Main(Configuration configuration) {
        this.jasmine = new RhinoBackend(configuration);
    }

    public void run() {
        jasmine.execute(new CliNotifier());
    }

    private static class CliNotifier implements Notifier {
        List<Failure> failures = new CopyOnWriteArrayList<Failure>();
        boolean testRun = false;

        @Override
        public void pass(It it) {
            System.out.print(".");
            testRun = true;
        }

        @Override
        public void fail(It it, Failure failure) {
            System.out.print("F");
            testRun = true;
            failures.add(failure);
        }

        @Override
        public void skipped(It it) {
            testRun = true;
            System.out.print("-");
        }

        @Override
        public void started(It it) {

        }

        @Override
        public void finished() {
            if (testRun) {
                System.out.println();
                System.out.println();

                for (Failure failure : failures) {
                    System.out.println(failure.getMessage());
                    System.out.println(failure.getStack());
                    System.out.println();
                }
            }
        }
    }
}
