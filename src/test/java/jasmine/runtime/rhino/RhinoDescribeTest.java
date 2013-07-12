package jasmine.runtime.rhino;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import jasmine.runtime.Address;
import jasmine.runtime.Describe;
import jasmine.runtime.It;
import jasmine.runtime.JasmineVisitor;
import org.junit.Test;
import org.mozilla.javascript.NativeObject;

import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.fest.assertions.Assertions.assertThat;

public class RhinoDescribeTest {
    @Test
    public void shouldHaveChildSpecs() {
        RhinoContext context = new RhinoContext();
        NativeObject object = (NativeObject) context.evalJS(
                "var object = {specs: function(){ return [{id: 1, suite: {id: 1}, description: 'CHILD'}]}}; object;"
        );

        RhinoDescribe describe = new RhinoDescribe(object, context);

        assertThat(describe.getIts()).hasSize(1);
        assertThat(describe.getIts().iterator().next().getDescription()).isEqualTo("CHILD");
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
    public void shouldRebindTreeToNewContext() {
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
            @Override
            public String apply(RhinoIt input) {
                return input.getDescription();
            }
        });

        assertThat(its).containsOnly("CHILD IT", "GRANDCHILD IT");
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

        assertThat(describe.getDescription()).isEqualTo("ROOT");
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

    private static class DescriptionTracingVisitor implements JasmineVisitor {
        private final List<String> sequence;
        private final Address<Describe> address;

        public DescriptionTracingVisitor() {
            this.sequence = newArrayList();
            this.address = new Address<Describe>();
        }

        private DescriptionTracingVisitor(List<String> sequence, Address<Describe> address) {
            this.sequence = sequence;
            this.address = address;
        }

        @Override
        public void visit(Describe describe) {
            sequence.add(describe.getDescription());
        }

        @Override
        public void visit(It it) {
            sequence.add(it.getDescription());
        }

        @Override
        public JasmineVisitor forNextLevel(Describe describe) {
            return new DescriptionTracingVisitor(sequence, address.push(describe));
        }

        @Override
        public Address<Describe> getCurrentAddress() {
            return address;
        }
    }
}
