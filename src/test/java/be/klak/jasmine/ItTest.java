package be.klak.jasmine;

import org.junit.Test;
import org.mozilla.javascript.NativeObject;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItTest {
    @Test
    public void shouldSpecsWithTheSameNameShouldNotHaveTheSameDescription() {
        NativeObject jsSpec1 = mock(NativeObject.class);
        when(jsSpec1.get("description", jsSpec1)).thenReturn("green");
        NativeObject jsSpec2 = mock(NativeObject.class);
        when(jsSpec2.get("description", jsSpec2)).thenReturn("green");

        It spec1 = new It(jsSpec1);
        It spec2 = new It(jsSpec2);

        assertThat(spec1.getDescription()).isNotEqualTo(spec2.getDescription());
    }
}
