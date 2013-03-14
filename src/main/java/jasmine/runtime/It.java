package jasmine.runtime;

public interface It {
    String getId();
    String getStringDescription();
    Describe getParent();
    void accept(JasmineVisitor visitor);
}
