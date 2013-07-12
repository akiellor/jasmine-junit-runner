package jasmine.runtime.rhino;

import jasmine.runtime.JasmineVisitor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mozilla.javascript.NativeObject;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RhinoItTest {
    @Mock
    NativeObject object;
    @Mock
    RhinoContext context;

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

        assertThat(it.getDescription()).isEqualTo("green");
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
    public void shouldAcceptVisitor() {
        NativeObject spec = mock(NativeObject.class);
        JasmineVisitor visitor = mock(JasmineVisitor.class);

        RhinoIt it = new RhinoIt(spec, context);

        it.accept(visitor);

        verify(visitor).visit(it);
    }
}
