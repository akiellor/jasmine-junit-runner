package jasmine.runtime;

import jasmine.runtime.Describe;
import jasmine.runtime.It;
import jasmine.runtime.rhino.RhinoContext;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.junit.Test;
import org.junit.runner.Description;
import org.mozilla.javascript.NativeObject;

import java.util.Collection;

import static com.google.common.collect.Lists.newArrayList;
import static org.fest.assertions.Assertions.assertThat;

public class DescribeTest {
    @Test
    public void shouldHaveDescription() {
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS("var object = {description: 'DESCRIPTION', suites: function(){ return []; }, specs: function(){ return [];}}; object;");

        Describe describe = new Describe(object, context);

        assertThat(describe.getDescription().getDisplayName()).isEqualTo("DESCRIPTION");
    }

    @Test
    public void shouldHaveConsistentDescription() {
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS("var object = {description: 'DESCRIPTION', suites: function(){ return []; }, specs: function(){ return [];}}; object;");

        Describe describe = new Describe(object, context);

        Description first = describe.getDescription();
        Description second = describe.getDescription();

        assertThat(first).isSameAs(second);
    }

    @Test
    public void shouldHaveChildDescribes() {
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS(
                "var object = {suites: function(){ return [{description: 'CHILD', suites: function(){ return []; }, specs: function(){ return [];}}]}}; object;"
        );

        Describe describe = new Describe(object, context);

        assertThat(describe.getDescribes()).hasSize(1);
        assertThat(describe.getDescribes().iterator().next().getDescription().getDisplayName()).isEqualTo("CHILD");
    }

    @Test
    public void shouldHaveDescriptionWithChildrenDescribes() {
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS(
                "var object = {description: 'ROOT', specs: function() { return []; }, suites: function(){ return [{description: 'CHILD', suites: function(){ return []; }, specs: function(){ return [];}}]}}; object;"
        );

        Describe describe = new Describe(object, context);

        assertThat(describe.getDescription().getChildren()).hasSize(1);
        assertThat(describe.getDescription().getChildren().get(0).getDisplayName()).isEqualTo("CHILD");
    }

    @Test
    public void shouldHaveDescriptionWithChildrenIts() {
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS(
                "var object = {description: 'ROOT', suites: function(){ return [];}, specs: function(){ return [{description: 'CHILD'}]}}; object;"
        );

        Describe describe = new Describe(object, context);

        assertThat(describe.getDescription().getChildren()).hasSize(1);
        assertThat(describe.getDescription().getChildren().get(0).getDisplayName()).isEqualTo("CHILD");
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

    @Test
    public void shouldTestBindingOfDescribe() {
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS(
                "var object = {specs: function(){ return [{description: 'CHILD'}]}}; object;"
        );

        Describe describe = new Describe(object, context);

        assertThat(describe.isBoundTo(context)).isTrue();
        assertThat(describe.isBoundTo(context.fork())).isFalse();
    }

    @Test
    public void shouldRebindTreeToNewContext(){
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS(
                "var object = {suites: function(){ return [{description: 'CHILD'}]}}; object;"
        );

        Describe describe = new Describe(object, context);

        RhinoContext newContext = context.fork();
        Describe newDescribe = describe.bind(newContext);

        assertThat(newDescribe.isBoundTo(newContext)).isTrue();
        assertThat(newDescribe.getDescribes().iterator().next().isBoundTo(newContext)).isTrue();
    }

    @Test
    public void shouldGetAllIts() {
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS(
                "var object = {specs: function(){ return [{description: 'CHILD IT'}]; }, suites: function(){ return [{description: 'CHILD DESCRIBE', suites: function(){ return []; }, specs: function(){ return [{description: 'GRANDCHILD IT'}]; }}]}}; object;"
        );

        Describe describe = new Describe(object, context);
        Collection<String> its = Collections2.transform(describe.getAllIts(), new Function<It, String>() {
            @Override public String apply(It input) {
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

        Describe describe = new Describe(object, context);
        Collection<String> its = newArrayList(Collections2.transform(describe.getDescription().getChildren(), new Function<Description, String>() {
            @Override public String apply(Description input) {
                return input.getDisplayName();
            }
        }));

        assertThat(its).isEqualTo(newArrayList("CHILD IT", "CHILD DESCRIBE"));
    }
}
