package jasmine.runtime;

public interface It {
    String getId();
    String getDescription();
    Describe getParent();
    void accept(JasmineVisitor visitor);
}
