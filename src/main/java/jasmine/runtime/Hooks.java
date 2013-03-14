package jasmine.runtime;

import jasmine.runtime.rhino.RhinoContext;

public interface Hooks {
    void beforeAll(RhinoContext context);
    void afterAll(RhinoContext context);
}
