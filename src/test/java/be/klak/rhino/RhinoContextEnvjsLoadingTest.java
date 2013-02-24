package be.klak.rhino;

import be.klak.junit.jasmine.JasmineTestRunner;
import org.junit.Test;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.tools.shell.Global;

import static org.fest.assertions.Assertions.assertThat;

public class RhinoContextEnvjsLoadingTest {

    @Test
    public void loadEnvJSShouldSetWindowSpaceAndBeES5Complaint() {
        RhinoContext context = new RhinoContext();

        context.loadFromVirtualFileSystem(JasmineTestRunner.ENV_JS_LIBRARY);
        context.loadFromVirtualFileSystem("src/test/javascript/envJsOptions.js");

        assertThat(context.evalJS("window")).isInstanceOf(Global.class);

        assertThat(context.evalJS("Object.create({ test: 'test' });")).isInstanceOf(NativeObject.class);
    }

    @Test(expected = EcmaError.class)
    public void failWithoutLoadingEnvAndManipulatingDOMStuff() {
        RhinoContext context = new RhinoContext();
        context.evalJS("document.getElementById");
    }

}
