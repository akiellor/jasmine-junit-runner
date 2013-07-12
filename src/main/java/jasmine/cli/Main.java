package jasmine.cli;

import com.google.common.collect.Lists;
import jasmine.runtime.Configuration;
import jasmine.runtime.Hooks;
import jasmine.runtime.It;
import jasmine.runtime.Notifier;
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
        private static class Failure{
            private final Throwable error;
            private final It it;

            public Failure(It it, Throwable error){
                this.it = it;
                this.error = error;
            }

            public String format() {
                StringBuilder builder = new StringBuilder()
                    .append(it.getId())
                    .append(" - ")
                    .append(it.getDescription())
                    .append("\n\n");


                builder.append(error.getMessage())
                        .append("\n\n");

                return builder.toString();
            }
        }

        List<Failure> failures = new CopyOnWriteArrayList<Failure>();
        boolean testRun = false;

        @Override
        public void pass(It it) {
            System.out.print(".");
            testRun = true;
        }

        @Override
        public void fail(It it, jasmine.runtime.Failure failure) {
            System.out.print("F");
            testRun = true;
            failures.add(new Failure(it, failure.getThrowable()));
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
                    System.out.println(failure.format());
                }
            }
        }
    }
}
