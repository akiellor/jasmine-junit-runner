package jasmine.runtime;

public interface Notifier {
    void pass(It it);
    void fail(It it, Throwable error);
    void fail(It it, Failure failure);
    void skipped(It it);
    void started(It it);
    void finished();
}
