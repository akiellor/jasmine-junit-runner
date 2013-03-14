package jasmine.runtime;

import jasmine.rhino.RhinoContext;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mozilla.javascript.NativeObject;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RunnerTest {
    @Mock Notifier notifier;

    @Test
    public void shouldGetDescribesFromRhinoContext() {
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS(
                "var object = {suites: function(){ return [{parentSuite: null, id: 1, description: 'CHILD', specs: function(){ return [];}, suites: function() { return []; }}]}}; object;"
        );

        Runner describe = new Runner(object, context, Description.createSuiteDescription("ROOT"));

        assertThat(describe.getDescription().getChildren()).hasSize(1);
        assertThat(describe.getDescription().getChildren().get(0).getDisplayName()).isEqualTo("CHILD");
    }

    @Test
    public void shouldHaveConsistentDescription() {
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS(
                "var object = {suites: function(){ return [{parentSuite: null, description: 'CHILD', specs: function(){ return [];}, suites: function() { return []; }}]}}; object;"
        );

        Runner describe = new Runner(object, context, Description.createSuiteDescription("ROOT"));

        Description first = describe.getDescription();
        Description second = describe.getDescription();

        assertThat(first).isSameAs(second);
    }

    @Test
    public void shouldReportNoSpecsToRun() {
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS(
                "var object = {suites: function(){ return [];}}; object;"
        );

        Runner runner = new Runner(object, context, Description.createSuiteDescription("ROOT"));

        runner.execute(notifier);

        verify(notifier).nothingToRun();
    }
}
