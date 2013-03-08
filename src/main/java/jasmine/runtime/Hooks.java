package jasmine.runtime;

import jasmine.rhino.RhinoContext;

public interface Hooks {
    void beforeAll(RhinoContext context);
    void afterAll(RhinoContext context);
}
