package be.klak.jasmine;

import be.klak.rhino.RhinoContext;

public interface Hooks {
    void beforeAll(RhinoContext context);
    void afterAll(RhinoContext context);
}
