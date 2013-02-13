package be.klak.junit.jasmine;

import org.junit.Test;
import org.mozilla.javascript.NativeObject;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JasmineSpecTest {
    @Test
    public void shouldSpecsWithTheSameNameShouldNotHaveTheSameDescription() {
        NativeObject jsSpec1 = mock(NativeObject.class);
        when(jsSpec1.get("description", jsSpec1)).thenReturn("green");
        NativeObject jsSpec2 = mock(NativeObject.class);
        when(jsSpec2.get("description", jsSpec2)).thenReturn("green");

        JasmineSpec spec1 = new JasmineSpec(jsSpec1);
        JasmineSpec spec2 = new JasmineSpec(jsSpec2);

        assertThat(spec1.getDescription()).isNotEqualTo(spec2.getDescription());
    }
}
