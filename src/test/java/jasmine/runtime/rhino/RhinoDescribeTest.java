package jasmine.runtime.rhino;

import jasmine.rhino.RhinoContext;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.junit.Test;
import org.junit.runner.Description;
import org.mozilla.javascript.NativeObject;

import java.util.Collection;

import static com.google.common.collect.Lists.newArrayList;
import static org.fest.assertions.Assertions.assertThat;

public class RhinoDescribeTest {
    @Test
    public void shouldHaveDescription() {
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS("var object = {description: 'DESCRIPTION', suites: function(){ return []; }, specs: function(){ return [];}}; object;");

        RhinoDescribe rhinoDescribe = new RhinoDescribe(object, context);

        assertThat(rhinoDescribe.getDescription().getDisplayName()).isEqualTo("DESCRIPTION");
    }

    @Test
    public void shouldHaveConsistentDescription() {
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS("var object = {description: 'DESCRIPTION', suites: function(){ return []; }, specs: function(){ return [];}}; object;");

        RhinoDescribe rhinoDescribe = new RhinoDescribe(object, context);

        Description first = rhinoDescribe.getDescription();
        Description second = rhinoDescribe.getDescription();

        assertThat(first).isSameAs(second);
    }

    @Test
    public void shouldHaveChildDescribes() {
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS(
                "var object = {suites: function(){ return [{description: 'CHILD', suites: function(){ return []; }, specs: function(){ return [];}}]}}; object;"
        );

        RhinoDescribe rhinoDescribe = new RhinoDescribe(object, context);

        assertThat(rhinoDescribe.getDescribes()).hasSize(1);
        assertThat(rhinoDescribe.getDescribes().iterator().next().getDescription().getDisplayName()).isEqualTo("CHILD");
    }

    @Test
    public void shouldHaveDescriptionWithChildrenDescribes() {
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS(
                "var object = {description: 'ROOT', specs: function() { return []; }, suites: function(){ return [{description: 'CHILD', suites: function(){ return []; }, specs: function(){ return [];}}]}}; object;"
        );

        RhinoDescribe rhinoDescribe = new RhinoDescribe(object, context);

        assertThat(rhinoDescribe.getDescription().getChildren()).hasSize(1);
        assertThat(rhinoDescribe.getDescription().getChildren().get(0).getDisplayName()).isEqualTo("CHILD");
    }

    @Test
    public void shouldHaveDescriptionWithChildrenIts() {
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS(
                "var object = {description: 'ROOT', suites: function(){ return [];}, specs: function(){ return [{description: 'CHILD'}]}}; object;"
        );

        RhinoDescribe rhinoDescribe = new RhinoDescribe(object, context);

        assertThat(rhinoDescribe.getDescription().getChildren()).hasSize(1);
        assertThat(rhinoDescribe.getDescription().getChildren().get(0).getDisplayName()).isEqualTo("CHILD");
    }

    @Test
    public void shouldHaveChildSpecs() {
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS(
                "var object = {specs: function(){ return [{description: 'CHILD'}]}}; object;"
        );

        RhinoDescribe rhinoDescribe = new RhinoDescribe(object, context);

        assertThat(rhinoDescribe.getIts()).hasSize(1);
        assertThat(rhinoDescribe.getIts().iterator().next().getDescription().getDisplayName()).isEqualTo("CHILD");
    }

    @Test
    public void shouldTestBindingOfDescribe() {
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS(
                "var object = {specs: function(){ return [{description: 'CHILD'}]}}; object;"
        );

        RhinoDescribe rhinoDescribe = new RhinoDescribe(object, context);

        assertThat(rhinoDescribe.isBoundTo(context)).isTrue();
        assertThat(rhinoDescribe.isBoundTo(context.fork())).isFalse();
    }

    @Test
    public void shouldRebindTreeToNewContext(){
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS(
                "var object = {suites: function(){ return [{description: 'CHILD'}]}}; object;"
        );

        RhinoDescribe rhinoDescribe = new RhinoDescribe(object, context);

        RhinoContext newContext = context.fork();
        RhinoDescribe newRhinoDescribe = rhinoDescribe.bind(newContext);

        assertThat(newRhinoDescribe.isBoundTo(newContext)).isTrue();
        assertThat(newRhinoDescribe.getDescribes().iterator().next().isBoundTo(newContext)).isTrue();
    }

    @Test
    public void shouldGetAllIts() {
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS(
                "var object = {specs: function(){ return [{description: 'CHILD IT'}]; }, suites: function(){ return [{description: 'CHILD DESCRIBE', suites: function(){ return []; }, specs: function(){ return [{description: 'GRANDCHILD IT'}]; }}]}}; object;"
        );

        RhinoDescribe rhinoDescribe = new RhinoDescribe(object, context);
        Collection<String> its = Collections2.transform(rhinoDescribe.getAllIts(), new Function<RhinoIt, String>() {
            @Override public String apply(RhinoIt input) {
                return input.getDescription().getDisplayName();
            }
        });

        assertThat(its).containsOnly("CHILD IT", "GRANDCHILD IT");
    }

    @Test
    public void shouldDefineItsBeforeDescribes() {
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS(
                "var object = {description: 'ROOT', suites: function(){ return [{description: 'CHILD DESCRIBE', suites: function(){ return []; }, specs: function(){ return []; }}]}, specs: function(){ return [{description: 'CHILD IT'}]; }}; object;"
        );

        RhinoDescribe rhinoDescribe = new RhinoDescribe(object, context);
        Collection<String> its = newArrayList(Collections2.transform(rhinoDescribe.getDescription().getChildren(), new Function<Description, String>() {
            @Override public String apply(Description input) {
                return input.getDisplayName();
            }
        }));

        assertThat(its).isEqualTo(newArrayList("CHILD IT", "CHILD DESCRIBE"));
    }
}
