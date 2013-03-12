package jasmine.runtime;

import org.junit.runner.Description;

public interface Backend {
    Description getRootDescription();

    void execute(Notifier notifier);
}
