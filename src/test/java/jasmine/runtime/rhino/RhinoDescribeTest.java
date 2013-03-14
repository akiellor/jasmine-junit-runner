package jasmine.runtime.rhino;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Collections2;
import jasmine.rhino.RhinoContext;
import jasmine.runtime.Describe;
import jasmine.runtime.It;
import jasmine.runtime.JasmineVisitor;
import org.junit.Test;
import org.junit.runner.Description;
import org.mozilla.javascript.NativeObject;

import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RhinoDescribeTest {
    @Test
    public void shouldHaveDescription() {
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS("var object = {description: 'DESCRIPTION', suites: function(){ return []; }, specs: function(){ return [];}}; object;");

        RhinoDescribe describe = new RhinoDescribe(object, context);

        assertThat(describe.getDescription().getDisplayName()).isEqualTo("DESCRIPTION");
    }

    @Test
    public void shouldHaveConsistentDescription() {
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS("var object = {description: 'DESCRIPTION', suites: function(){ return []; }, specs: function(){ return [];}}; object;");

        RhinoDescribe describe = new RhinoDescribe(object, context);

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

        RhinoDescribe describe = new RhinoDescribe(object, context);

        assertThat(describe.getDescribes()).hasSize(1);
        assertThat(describe.getDescribes().iterator().next().getDescription().getDisplayName()).isEqualTo("CHILD");
    }

    @Test
    public void shouldHaveDescriptionWithChildrenDescribes() {
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS(
                "var object = {description: 'ROOT', specs: function() { return []; }, suites: function(){ return [{description: 'CHILD', suites: function(){ return []; }, specs: function(){ return [];}}]}}; object;"
        );

        RhinoDescribe describe = new RhinoDescribe(object, context);

        assertThat(describe.getDescription().getChildren()).hasSize(1);
        assertThat(describe.getDescription().getChildren().get(0).getDisplayName()).isEqualTo("CHILD");
    }

    @Test
    public void shouldHaveDescriptionWithChildrenIts() {
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS(
                "var object = {description: 'ROOT', suites: function(){ return [];}, specs: function(){ return [{id: 1, suite: {id: 1}, description: 'CHILD'}]}}; object;"
        );

        RhinoDescribe describe = new RhinoDescribe(object, context);

        assertThat(describe.getDescription().getChildren()).hasSize(1);
        assertThat(describe.getDescription().getChildren().get(0).getDisplayName()).isEqualTo("CHILD");
    }

    @Test
    public void shouldHaveChildSpecs() {
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS(
                "var object = {specs: function(){ return [{id: 1, suite: {id: 1}, description: 'CHILD'}]}}; object;"
        );

        RhinoDescribe describe = new RhinoDescribe(object, context);

        assertThat(describe.getIts()).hasSize(1);
        assertThat(describe.getIts().iterator().next().getDescription().getDisplayName()).isEqualTo("CHILD");
    }

    @Test
    public void shouldTestBindingOfDescribe() {
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS(
                "var object = {specs: function(){ return [{description: 'CHILD'}]}}; object;"
        );

        RhinoDescribe describe = new RhinoDescribe(object, context);

        assertThat(describe.isBoundTo(context)).isTrue();
        assertThat(describe.isBoundTo(context.fork())).isFalse();
    }

    @Test
    public void shouldRebindTreeToNewContext(){
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS(
                "var object = {suites: function(){ return [{description: 'CHILD'}]}}; object;"
        );

        RhinoDescribe describe = new RhinoDescribe(object, context);

        RhinoContext newContext = context.fork();
        RhinoDescribe newDescribe = describe.bind(newContext);

        assertThat(newDescribe.isBoundTo(newContext)).isTrue();
        assertThat(newDescribe.getDescribes().iterator().next().isBoundTo(newContext)).isTrue();
    }

    @Test
    public void shouldGetAllIts() {
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS(
                "var object = {specs: function(){ return [{id: 1, suite: {id: 1}, description: 'CHILD IT'}]; }, suites: function(){ return [{description: 'CHILD DESCRIBE', suites: function(){ return []; }, specs: function(){ return [{id: 1, suite: {id: 2}, description: 'GRANDCHILD IT'}]; }}]}}; object;"
        );

        RhinoDescribe describe = new RhinoDescribe(object, context);
        Collection<String> its = Collections2.transform(describe.getAllIts(), new Function<RhinoIt, String>() {
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
                "var object = {description: 'ROOT', suites: function(){ return [{id: 1, suite: {id: 1}, description: 'CHILD DESCRIBE', suites: function(){ return []; }, specs: function(){ return []; }}]}, specs: function(){ return [{id: 1, suite: {id: 2}, description: 'CHILD IT'}]; }}; object;"
        );

        RhinoDescribe describe = new RhinoDescribe(object, context);
        Collection<String> its = newArrayList(Collections2.transform(describe.getDescription().getChildren(), new Function<Description, String>() {
            @Override public String apply(Description input) {
                return input.getDisplayName();
            }
        }));

        assertThat(its).isEqualTo(newArrayList("CHILD IT", "CHILD DESCRIBE"));
    }

    @Test
    public void shouldHaveAnId() {
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS(
                "var object = {id: \"1\", description: 'ROOT', suites: function(){ return [];}, specs: function(){ return []; }}; object;"
        );

        RhinoDescribe describe = new RhinoDescribe(object, context);

        assertThat(describe.getId()).isEqualTo("1");
    }

    @Test
    public void shouldHaveAStringDescription() {
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS(
                "var object = {id: \"1\", description: 'ROOT', suites: function(){ return [];}, specs: function(){ return []; }}; object;"
        );

        RhinoDescribe describe = new RhinoDescribe(object, context);

        assertThat(describe.getStringDescription()).isEqualTo("ROOT");
    }

    @Test
    public void shouldHaveParentDescribe() {
        RhinoContext context = mock(RhinoContext.class);
        NativeObject parent = mock(NativeObject.class);
        NativeObject suite = mock(NativeObject.class);
        when(suite.get("parentSuite", suite)).thenReturn(parent);

        RhinoDescribe describe = new RhinoDescribe(suite, context);

        assertThat(describe.getParent()).isEqualTo(Optional.of(new RhinoDescribe(parent, context)));
    }

    @Test
    public void shouldNotHaveParentDescribe() {
        RhinoContext context = mock(RhinoContext.class);
        NativeObject suite = mock(NativeObject.class);
        when(suite.get("parentSuite", suite)).thenReturn(null);

        RhinoDescribe describe = new RhinoDescribe(suite, context);

        assertThat(describe.getParent()).isEqualTo(Optional.absent());
    }

    @Test
    public void shouldAcceptVisitor() {
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS(
                "var object = {description: 'ROOT', suites: function(){ return [{id: 1, suite: {id: 1}, description: 'CHILD DESCRIBE', suites: function(){ return []; }, specs: function(){ return []; }}]}, specs: function(){ return [{id: 1, suite: {id: 2}, description: 'CHILD IT'}]; }}; object;"
        );

        RhinoDescribe describe = new RhinoDescribe(object, context);

        DescriptionTracingVisitor visitor = new DescriptionTracingVisitor();
        describe.accept(visitor);

        assertThat(visitor.sequence).isEqualTo(
                newArrayList("ROOT", "CHILD IT", "CHILD DESCRIBE")
        );
    }

    private static class DescriptionTracingVisitor implements JasmineVisitor{
        List<String> sequence = newArrayList();

        @Override public void visit(Describe describe) {
            sequence.add(describe.getStringDescription());
        }

        @Override public void visit(It it) {
            sequence.add(it.getStringDescription());
        }
    }
}
