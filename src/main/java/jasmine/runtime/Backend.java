package jasmine.runtime;

public interface Backend {
    void accept(JasmineVisitor visitor);

    void execute(Notifier notifier);
}
