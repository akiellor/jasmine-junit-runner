package be.klak.jasmine;

import be.klak.rhino.RhinoContext;
import org.junit.Test;
import org.mozilla.javascript.NativeObject;

import static org.fest.assertions.Assertions.assertThat;

public class DescribeTest {
    @Test
    public void shouldHaveDescription() {
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS("var object = {description: 'DESCRIPTION'}; object;");

        Describe describe = new Describe(object, context);

        assertThat(describe.getDescription()).isEqualTo("DESCRIPTION");
    }

    @Test
    public void shouldHaveChildDescribes() {
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS(
                "var object = {suites: function(){ return [{description: 'CHILD'}]}}; object;"
        );

        Describe describe = new Describe(object, context);

        assertThat(describe.getDescribes()).hasSize(1);
        assertThat(describe.getDescribes().iterator().next().getDescription()).isEqualTo("CHILD");
    }

    @Test
    public void shouldHaveChildSpecs() {
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS(
                "var object = {specs: function(){ return [{description: 'CHILD'}]}}; object;"
        );

        Describe describe = new Describe(object, context);

        assertThat(describe.getIts()).hasSize(1);
        assertThat(describe.getIts().iterator().next().getDescription().getDisplayName()).isEqualTo("CHILD");
    }
}
