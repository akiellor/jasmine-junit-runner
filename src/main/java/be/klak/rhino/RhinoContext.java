package be.klak.rhino;

import be.klak.junit.jasmine.Loader;
import be.klak.junit.resources.Resource;
import org.mozilla.javascript.*;
import org.mozilla.javascript.tools.shell.Global;

import java.util.List;

public class RhinoContext {

	private final Context jsContext;
	private final Scriptable jsScope;
    private final Loader loader;

    public RhinoContext() {
		this.jsContext = createJavascriptContext();
		this.jsScope = createJavascriptScopeForContext(this.jsContext);
        this.loader = new Loader(jsScope, jsContext);
	}

	public RhinoContext(Scriptable sharedScope) {
		this.jsContext = createJavascriptContext();
		Scriptable newScope = this.jsContext.newObject(sharedScope);
		newScope.setPrototype(sharedScope);
		newScope.setParentScope(null);

		this.jsScope = newScope;
        this.loader = new Loader(jsScope, jsContext);
    }

	private RhinoContext createNewRhinoContextBasedOnPrevious() {
		return new RhinoContext(this.jsScope);
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
		return executeFunction(object, fnName, new Object[] {});
	}

	private Global createJavascriptScopeForContext(Context jsContext) {
		Global scope = new Global();
		scope.init(jsContext);
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

    public void load(Resource... resources) {
        loader.load(resources);
    }

    public void load(String... paths) {
        loader.load(paths);
    }
}
