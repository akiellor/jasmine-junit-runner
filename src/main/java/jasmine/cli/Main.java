package jasmine.cli;

import com.google.common.collect.Lists;
import jasmine.runtime.*;
import jasmine.runtime.rhino.RhinoBackend;
import jasmine.runtime.rhino.RhinoContext;

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
            if(testRun){
                System.out.println();
                System.out.println();

                for(Failure failure : failures){
                    System.out.println(failure.getMessage());
                    System.out.println(failure.getStack());
                    System.out.println();
                }
            }
        }
    }
}
