package jasmine.runtime.rhino;

import jasmine.rhino.RhinoContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mozilla.javascript.NativeObject;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RhinoItTest {
    @Mock NativeObject object;
    @Mock RhinoContext context;

    @Test
    public void shouldSpecsWithTheSameNameShouldNotHaveTheSameDescription() {
        NativeObject jsSpec1 = mock(NativeObject.class);
        when(jsSpec1.get("description", jsSpec1)).thenReturn("green");
        NativeObject jsSpec2 = mock(NativeObject.class);
        when(jsSpec2.get("description", jsSpec2)).thenReturn("green");

        RhinoIt spec1 = new RhinoIt(jsSpec1, context);
        RhinoIt spec2 = new RhinoIt(jsSpec2, context);

        assertThat(spec1.getDescription()).isNotEqualTo(spec2.getDescription());
    }

    @Test
    public void shouldVerifyIfBoundToContext() {
        RhinoContext context1 = mock(RhinoContext.class);
        RhinoContext context2 = mock(RhinoContext.class);

        RhinoIt rhinoIt = new RhinoIt(object, context1);

        assertThat(rhinoIt.isBoundTo(context1)).isTrue();
        assertThat(rhinoIt.isBoundTo(context2)).isFalse();
    }

    @Test
    public void shouldRebindItToNewContext() {
        RhinoContext context1 = mock(RhinoContext.class);
        RhinoContext context2 = mock(RhinoContext.class);

        RhinoIt rhinoIt = new RhinoIt(object, context1);

        assertThat(rhinoIt.isBoundTo(context1)).isTrue();
        assertThat(rhinoIt.isBoundTo(context2)).isFalse();

        rhinoIt = rhinoIt.bind(context2);

        assertThat(rhinoIt.isBoundTo(context1)).isFalse();
        assertThat(rhinoIt.isBoundTo(context2)).isTrue();
    }
}
