package jasmine.runtime;

import jasmine.rhino.RhinoContext;
import org.junit.Test;
import org.junit.runner.Description;
import org.mozilla.javascript.NativeObject;

import static org.fest.assertions.Assertions.assertThat;

public class RunnerTest {
    @Test
    public void shouldGetDescribesFromRhinoContext() {
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS(
                "var object = {suites: function(){ return [{description: 'CHILD', specs: function(){ return [];}, suites: function() { return []; }}]}}; object;"
        );

        Runner describe = new Runner(object, context, Description.createSuiteDescription("ROOT"));

        assertThat(describe.getDescription().getChildren()).hasSize(1);
        assertThat(describe.getDescription().getChildren().get(0).getDisplayName()).isEqualTo("CHILD");
    }

    @Test
    public void shouldHaveConsistentDescription() {
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS(
                "var object = {suites: function(){ return [{description: 'CHILD', specs: function(){ return [];}, suites: function() { return []; }}]}}; object;"
        );

        Runner describe = new Runner(object, context, Description.createSuiteDescription("ROOT"));

        Description first = describe.getDescription();
        Description second = describe.getDescription();

        assertThat(first).isSameAs(second);
    }
}
