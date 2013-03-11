package jasmine.runtime;

public interface Notifier {
    void pass(It it);
    void fail(It it);
    void skipped(It it);
    void started(It it);
    void nothingToRun();
}
