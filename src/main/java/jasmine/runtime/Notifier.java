package jasmine.runtime;

import jasmine.runtime.rhino.RhinoIt;

public interface Notifier {
    void pass(RhinoIt it);
    void fail(RhinoIt it);
    void skipped(RhinoIt it);
    void started(RhinoIt it);
    void nothingToRun();
}
