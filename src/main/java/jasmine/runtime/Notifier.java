package jasmine.runtime;

public interface Notifier {
    void pass(It rhinoIt);
    void fail(It rhinoIt);
    void skipped(It rhinoIt);
    void started(It rhinoIt);
    void nothingToRun();
}
