package jasmine.runtime;

public interface JasmineVisitor {
    void visit(Describe describe);
    void visit(It it);
}
