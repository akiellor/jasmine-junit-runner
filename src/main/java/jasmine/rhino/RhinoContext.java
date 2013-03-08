package jasmine.rhino;

import com.google.common.base.Predicate;
import jasmine.utils.Exceptions;
import org.mozilla.javascript.*;
import org.mozilla.javascript.tools.shell.Global;
import org.reflections.vfs.Vfs;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class RhinoContext {

    private final Context jsContext;
    private final Scriptable jsScope;
    private final Loader loader;
    private final VirtualFileSystem fileSystem;

    public RhinoContext() {
        this(new ArrayList<String>());
    }

    public RhinoContext(Iterable<String> paths) {
        this.fileSystem = new VirtualFileSystem(paths, new Predicate<Vfs.File>() {
            @Override public boolean apply(@Nullable Vfs.File input) {
                return input != null && input.getRelativePath().endsWith("js");
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
            InputStream loader = Thread.currentThread().getContextClassLoader().getResourceAsStream("js/lib/loader.js");
            jsContext.evaluateReader(scope, new InputStreamReader(loader), "loader", 1, null);
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

    public void loadFromVirtualFileSystem(String... paths) {
        loadFromVirtualFileSystem(asList(paths));
    }

    public void loadFromVirtualFileSystem(List<String> paths){
        loader.loadFromVirtualFileSystem(paths);
    }

    public RhinoContext fork() {
        return new RhinoContext(this.jsScope, this.fileSystem);
    }
}
