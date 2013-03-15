package jasmine.runtime;

public interface JasmineVisitor {
    void visit(Describe describe);
    void visit(It it);

    JasmineVisitor forNextLevel(Describe describe);
    Address<Describe> getCurrentAddress();
}
