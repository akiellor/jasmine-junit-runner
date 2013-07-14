package jasmine.cli;

import jasmine.runtime.Configuration;
import jasmine.runtime.Failure;
import jasmine.runtime.It;
import jasmine.runtime.Notifier;
import jasmine.runtime.rhino.RhinoBackend;

import java.io.PrintStream;
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
        jasmine.execute(new CliNotifier(new Terminal.Colour(System.out)));
    }

    private static interface Terminal {
        void print(ColourCode colour, String message);

        static final char ESCAPE = 27;

        enum ColourCode{
            GREEN(String.format("%s[32m", ESCAPE)),
            RED(String.format("%s[31m", ESCAPE));

            private String code;

            ColourCode(String code) {
                this.code = code;
            }
        }

        static class Colour implements Terminal {
            private PrintStream stream;

            Colour(PrintStream stream){
                this.stream = stream;
            }

            @Override
            public void print(ColourCode colour, String message) {
                stream.print(colour.code);
                stream.print(message);
                stream.print(String.format("%s[0m", ESCAPE));
            }
        }
    }

    private static class CliNotifier implements Notifier {
        private final Terminal terminal;
        private final List<Failure> failures = new CopyOnWriteArrayList<Failure>();
        boolean testRun = false;

        public CliNotifier(Terminal terminal) {
            this.terminal = terminal;
        }

        @Override
        public void pass(It it) {
            terminal.print(Terminal.ColourCode.GREEN, ".");
            testRun = true;
        }

        @Override
        public void fail(It it, Failure failure) {
            terminal.print(Terminal.ColourCode.RED, "F");
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
                terminal.print(Terminal.ColourCode.RED, "\n\n");

                for (Failure failure : failures) {
                    terminal.print(Terminal.ColourCode.RED, failure.getMessage() + "\n");
                    terminal.print(Terminal.ColourCode.RED, failure.getStack() + "\n");
                }
            }
        }
    }
}
