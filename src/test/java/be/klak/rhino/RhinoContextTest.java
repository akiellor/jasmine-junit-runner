package be.klak.rhino;

import be.klak.junit.resources.ClasspathResource;
import org.junit.Test;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptableObject;

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

    @Test
    public void runAsyncUsesTheSameSharedGlobalScope() throws InterruptedException {
        RhinoContext baseContext = new RhinoContext();
        baseContext.evalJS("var base = 'base'");

        baseContext.runAsync(new RhinoRunnable() {

            @Override
            public void run(RhinoContext context) {
                assertThat(context.evalJS("base")).isEqualTo("base");
            }
        });

        Thread.sleep(500);
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
    public void loadsJSFilesFromClasspath() {
        RhinoContext context = new RhinoContext();
        context.loadFromClasspath("js/lib/loadsJSFilesFromClasspathTarget.js");

        assertThat(context.evalJS("target.theAnswer")).isEqualTo("forty two");
    }

    @Test
    public void loadsClasspathResourceFromClasspath() {
        RhinoContext context = new RhinoContext();
        context.load(new ClasspathResource("js/lib/loadsJSFilesFromClasspathTarget.js"));

        assertThat(context.evalJS("target.theAnswer")).isEqualTo("forty two");
    }

    @Test
    public void loadsManyClasspathResourcesFromClasspath() {
        RhinoContext context = new RhinoContext();
        context.load(
                new ClasspathResource("js/lib/loadsJSFilesFromClasspathTarget.js"),
                new ClasspathResource("js/lib/anotherLoadsJSFilesFromClasspathTarget.js")
        );

        assertThat(context.evalJS("target.theAnswer")).isEqualTo("forty two");
        assertThat(context.evalJS("anotherTarget.theAnswer")).isEqualTo("fifty six");
    }
}
