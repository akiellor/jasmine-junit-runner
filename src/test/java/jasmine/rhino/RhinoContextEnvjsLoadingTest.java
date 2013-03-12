package jasmine.rhino;

import jasmine.runtime.rhino.RhinoRuntime;
import org.junit.Test;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.tools.shell.Global;

import static org.fest.assertions.Assertions.assertThat;

public class RhinoContextEnvjsLoadingTest {

    @Test
    public void loadEnvJSShouldSetWindowSpaceAndBeES5Complaint() {
        RhinoContext context = new RhinoContext();

        context.loadFromVirtualFileSystem(RhinoRuntime.ENV_JS_LIBRARY);
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
