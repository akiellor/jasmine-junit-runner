package jasmine.runtime.rhino;

import jasmine.rhino.RhinoContext;
import jasmine.runtime.JasmineVisitor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mozilla.javascript.NativeObject;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RhinoItTest {
    @Mock NativeObject object;
    @Mock RhinoContext context;

    @Test
    public void shouldSpecsWithTheSameNameShouldNotHaveTheSameDescription() {
        NativeObject suite = mock(NativeObject.class);
        when(suite.get("id", suite)).thenReturn(0);
        NativeObject jsSpec1 = mock(NativeObject.class);
        when(jsSpec1.get("id", jsSpec1)).thenReturn(0);
        when(jsSpec1.get("description", jsSpec1)).thenReturn("green");
        when(jsSpec1.get("suite", jsSpec1)).thenReturn(suite);
        NativeObject jsSpec2 = mock(NativeObject.class);
        when(jsSpec2.get("id", jsSpec2)).thenReturn(1);
        when(jsSpec2.get("description", jsSpec2)).thenReturn("green");
        when(jsSpec2.get("suite", jsSpec2)).thenReturn(suite);

        RhinoIt spec1 = new RhinoIt(jsSpec1, context);
        RhinoIt spec2 = new RhinoIt(jsSpec2, context);

        assertThat(spec1.getDescription()).isNotEqualTo(spec2.getDescription());
    }

    @Test
    public void shouldHaveId() {
        NativeObject suite = mock(NativeObject.class);
        when(suite.get("id", suite)).thenReturn(0);
        NativeObject spec = mock(NativeObject.class);
        when(spec.get("id", spec)).thenReturn(1);
        when(spec.get("suite", spec)).thenReturn(suite);

        RhinoIt it = new RhinoIt(spec, context);

        assertThat(it.getId()).isEqualTo("0-1");
    }

    @Test
    public void shouldHaveStringDescription() {
        NativeObject spec = mock(NativeObject.class);
        when(spec.get("description", spec)).thenReturn("green");

        RhinoIt it = new RhinoIt(spec, context);

        assertThat(it.getStringDescription()).isEqualTo("green");
    }

    @Test
    public void shouldVerifyIfBoundToContext() {
        RhinoContext context1 = mock(RhinoContext.class);
        RhinoContext context2 = mock(RhinoContext.class);

        RhinoIt it = new RhinoIt(object, context1);

        assertThat(it.isBoundTo(context1)).isTrue();
        assertThat(it.isBoundTo(context2)).isFalse();
    }

    @Test
    public void shouldRebindItToNewContext() {
        RhinoContext context1 = mock(RhinoContext.class);
        RhinoContext context2 = mock(RhinoContext.class);

        RhinoIt it = new RhinoIt(object, context1);

        assertThat(it.isBoundTo(context1)).isTrue();
        assertThat(it.isBoundTo(context2)).isFalse();

        it = it.bind(context2);

        assertThat(it.isBoundTo(context1)).isFalse();
        assertThat(it.isBoundTo(context2)).isTrue();
    }

    @Test
    public void shouldHaveSuite() {
        NativeObject suite = mock(NativeObject.class);
        when(suite.get("id", suite)).thenReturn(0);
        NativeObject spec = mock(NativeObject.class);
        when(spec.get("id", spec)).thenReturn(1);
        when(spec.get("suite", spec)).thenReturn(suite);

        RhinoIt it = new RhinoIt(spec, context);

        assertThat(it.getParent()).isEqualTo(new RhinoDescribe(suite, context));
    }

    @Test
    public void shouldAcceptVisitor() {
        NativeObject spec = mock(NativeObject.class);
        JasmineVisitor visitor = mock(JasmineVisitor.class);

        RhinoIt it = new RhinoIt(spec, context);

        it.accept(visitor);

        verify(visitor).visit(it);
    }
}
