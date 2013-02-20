package be.klak.rhino;

import be.klak.junit.jasmine.Loader;
import be.klak.junit.jasmine.VirtualFileSystem;
import be.klak.junit.resources.ClasspathResource;
import be.klak.junit.resources.Resource;
import be.klak.utils.Exceptions;
import com.google.common.base.Predicate;
import org.mozilla.javascript.*;
import org.mozilla.javascript.tools.shell.Global;
import org.reflections.vfs.Vfs;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class RhinoContext {

    private final Context jsContext;
    private final Scriptable jsScope;
    private final Loader loader;
    private final VirtualFileSystem fileSystem;

    public RhinoContext() {
        this.fileSystem = new VirtualFileSystem(new Predicate<Vfs.File>() {
            @Override public boolean apply(@Nullable Vfs.File input) {
                return input.getRelativePath().endsWith("js");
            }
        });

        this.jsContext = createJavascriptContext();
        this.jsScope = createJavascriptScopeForContext(this.jsContext);
        this.loader = new Loader(jsScope, jsContext, fileSystem);
    }

    public RhinoContext(Scriptable sharedScope, VirtualFileSystem fileSystem) {
        this.fileSystem = fileSystem;
        this.jsContext = createJavascriptContext();
        Scriptable newScope = this.jsContext.newObject(sharedScope);
        newScope.setPrototype(sharedScope);
        newScope.setParentScope(null);

        this.jsScope = newScope;
        this.loader = new Loader(jsScope, jsContext, fileSystem);
    }

    private RhinoContext createNewRhinoContextBasedOnPrevious() {
        return new RhinoContext(this.jsScope, this.fileSystem);
    }

    public void runAsync(final RhinoRunnable runnable) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                RhinoContext newRhinoContextBasedOnPrevious = createNewRhinoContextBasedOnPrevious();
                try {
                    runnable.run(newRhinoContextBasedOnPrevious);
                } finally {
                    newRhinoContextBasedOnPrevious.exit();
                }
            }
        }).start();
    }

    public Object evalJS(String js) {
        return this.jsContext.evaluateString(this.jsScope, js, "script", 1, null);
    }

    public void setProperty(String objectToReceiveProperty, String property, Object value) {
        Object obj = evalJS(objectToReceiveProperty);
        if (obj == null || !(obj instanceof ScriptableObject)) {
            throw new IllegalStateException("object to receive property is no ScriptableObject but a "
                    + (obj == null ? "" : obj.getClass().getSimpleName()));
        }

        ScriptableObject objectToReceive = (ScriptableObject) obj;
        objectToReceive.put(property, objectToReceive, value);
    }

    public Object executeFunction(ScriptableObject object, String fnName, Object[] arguments) {
        Object fnPointer = object.get(fnName, object);
        if (fnPointer == null || !(fnPointer instanceof Function)) {
            fnPointer = object.getPrototype().get(fnName, object);
        }

        return ((Function) fnPointer).call(jsContext, jsScope, object, arguments);
    }

    public Object executeFunction(ScriptableObject object, String fnName) {
        return executeFunction(object, fnName, new Object[]{});
    }

    private Scriptable createJavascriptScopeForContext(Context jsContext) {
        Global scope = new Global();
        scope.init(jsContext);

        ScriptableObject.putProperty(scope, "__VIRTUAL_FILESYSTEM__", this.fileSystem);

        try {
            jsContext.evaluateReader(scope, new InputStreamReader(new ClasspathResource("js/lib/loader.js").getURL().openStream()), "loader", 1, null);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }

        return scope;
    }

    private Context createJavascriptContext() {
        Context jsContext = ContextFactory.getGlobal().enterContext();
        jsContext.setOptimizationLevel(-1);
        jsContext.setLanguageVersion(Context.VERSION_1_8);
        jsContext.setErrorReporter(new ChainedErrorReporter(jsContext.getErrorReporter()));
        return jsContext;
    }

    public void exit() {
        Context.exit();
    }

    public void load(List<? extends Resource> resources) {
        loader.load(resources);
    }

    public void load(Resource resource) {
        loader.load(resource);
    }

    public void load(Resource... resources) {
        loader.load(resources);
    }

    public void load(String... paths) {
        loader.load(paths);
    }

    public void loadFromVirtualFileSystem(String... paths) {
        loader.loadFromVirtualFileSystem(paths);
    }
}
