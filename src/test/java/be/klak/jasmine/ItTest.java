package be.klak.jasmine;

import be.klak.rhino.RhinoContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mozilla.javascript.NativeObject;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ItTest {
    @Mock NativeObject object;
    @Mock RhinoContext context;

    @Test
    public void shouldSpecsWithTheSameNameShouldNotHaveTheSameDescription() {
        NativeObject jsSpec1 = mock(NativeObject.class);
        when(jsSpec1.get("description", jsSpec1)).thenReturn("green");
        NativeObject jsSpec2 = mock(NativeObject.class);
        when(jsSpec2.get("description", jsSpec2)).thenReturn("green");

        It spec1 = new It(jsSpec1, context);
        It spec2 = new It(jsSpec2, context);

        assertThat(spec1.getDescription()).isNotEqualTo(spec2.getDescription());
    }

    @Test
    public void shouldVerifyIfBoundToContext() {
        RhinoContext context1 = mock(RhinoContext.class);
        RhinoContext context2 = mock(RhinoContext.class);

        It it = new It(object, context1);

        assertThat(it.isBoundTo(context1)).isTrue();
        assertThat(it.isBoundTo(context2)).isFalse();
    }

    @Test
    public void shouldRebindItToNewContext() {
        RhinoContext context1 = mock(RhinoContext.class);
        RhinoContext context2 = mock(RhinoContext.class);

        It it = new It(object, context1);

        assertThat(it.isBoundTo(context1)).isTrue();
        assertThat(it.isBoundTo(context2)).isFalse();

        it = it.bind(context2);

        assertThat(it.isBoundTo(context1)).isFalse();
        assertThat(it.isBoundTo(context2)).isTrue();
    }
}
