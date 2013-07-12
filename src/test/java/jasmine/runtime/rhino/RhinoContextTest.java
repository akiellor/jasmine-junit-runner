package jasmine.runtime.rhino;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Timer;
import org.junit.Test;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptableObject;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.Lists.newArrayList;
import static org.fest.assertions.Assertions.assertThat;

public class RhinoContextTest {

    @Test
    public void executeFunctionOnPrototypeAndActualObject() {
        RhinoContext context = new RhinoContext();
        String js = "" +
                "var obj = function() {" +
                "  this.actual = function() { " +
                "    return 5; " +
                "  }" +
                "};" +
                "obj.prototype = {" +
                "   go : function() {" +
                "    return 3; " +
                "   }" +
                "}";

        context.evalJS(js);
        ScriptableObject obj = (ScriptableObject) context.evalJS("new obj()");

        assertThat(context.executeFunction(obj, "go")).isEqualTo(3.0);
        assertThat(context.executeFunction(obj, "actual")).isEqualTo(5.0);
    }

    @Test(expected = IllegalStateException.class)
    public void setPropertyOnUndefinedNotPossible() {
        RhinoContext context = new RhinoContext();
        context.evalJS("var zever");
        context.setProperty("zever", "prop", 1);
    }

    @Test(expected = IllegalStateException.class)
    public void setPropertyOnNullNotPossible() {
        RhinoContext context = new RhinoContext();
        context.setProperty(null, null, null);
    }

    @Test
    public void setPropertyOnSomeObj() {
        RhinoContext context = new RhinoContext();

        NativeObject anObj = (NativeObject) context.evalJS("var obj = { a: 'a'}; obj");
        context.setProperty("obj", "b", "b");

        assertThat(anObj.get("b", anObj)).isEqualTo("b");
    }

    @Test
    public void shouldBeAbleToLoadFromClasspathFromWithinContext() {
        RhinoContext context = new RhinoContext();

        Object actual = context.evalJS("load('js/lib/loadsJSFilesFromClasspathTarget.js'); target.theAnswer;");

        assertThat(actual).isEqualTo("forty two");
    }

    @Test
    public void shouldLoadFromClasspathViaVirtualFileSystem() {
        RhinoContext context = new RhinoContext();

        context.loadFromVirtualFileSystem("js/lib/loadsJSFilesFromClasspathTarget.js");

        assertThat(context.evalJS("target.theAnswer")).isEqualTo("forty two");
    }

    @Test
    public void shouldLoadFromFileSystemViaVirtualFileSystem() {
        RhinoContext context = new RhinoContext();

        context.loadFromVirtualFileSystem("src/test/javascript/sources/source1.js");

        assertThat(context.evalJS("source1")).isEqualTo(1.0);
    }

    @Test
    public void shouldAllowSpecifyingASetOfSearchPathsForRequiringFiles() {
        RhinoContext context = new RhinoContext(newArrayList("src/test/javascript"));

        context.loadFromVirtualFileSystem("sources/source1.js");

        assertThat(context.evalJS("source1")).isEqualTo(1.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToLoadFromVirtualFileSystem() {
        new RhinoContext().loadFromVirtualFileSystem("some/unknown/file.js");
    }

    @Test
    public void shouldTimeTheLoadSpeed() throws Exception {
        final RhinoContext context = new RhinoContext();

        final Timer loads = Metrics.newTimer(RhinoContext.class, "loads", TimeUnit.MILLISECONDS, TimeUnit.SECONDS);

        for (int i = 0; i < 100; i++) {
            loads.time(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    context.fork().loadFromVirtualFileSystem("src/test/javascript/sources/source1.js");
                    return null;
                }
            });
        }

        loads.stop();

        assertThat(loads.mean()).isLessThan(5.0);
    }
}
