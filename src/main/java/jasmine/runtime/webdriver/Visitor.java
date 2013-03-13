package jasmine.runtime.webdriver;

public interface Visitor {
    void visit(RunnerHandler.HttpSuite suite);

    void visit(RunnerHandler.HttpSpec spec);
}
