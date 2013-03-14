package jasmine.runtime.rhino;

import jasmine.rhino.RhinoContext;
import jasmine.runtime.*;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;

import static org.junit.Assert.assertTrue;

public class RhinoIt implements It {
    private static final int SLEEP_TIME_MILISECONDS = 50;

    private final RhinoContext context;
    private final NativeObject spec;

    public RhinoIt(NativeObject spec, RhinoContext context) {
        this.spec = spec;
        this.context = context;
    }

    @Override public String getId() {
        NativeObject parentSuite = (NativeObject)spec.get("suite", spec);
        String suiteId = String.valueOf(parentSuite.get("id", parentSuite));
        String specId = String.valueOf(spec.get("id", spec));
        return suiteId + "-" + specId;
    }

    @Override public String getDescription() {
        return String.valueOf(spec.get("description", spec));
    }

    @Override public Describe getParent() {
        NativeObject suite = (NativeObject) spec.get("suite", spec);
        return new RhinoDescribe(suite, context);
    }

    public void accept(JasmineVisitor visitor) {
        visitor.visit(this);
    }

    public Status getSpecResultStatus() {
        assertTrue(isDone());

        NativeObject results = getSpecResults();
        boolean passed = (Boolean) context.executeFunction(results, "passed");
        boolean skipped = (Boolean) results.get("skipped", results);

        if (skipped) {
            return new Status.Skipped(this);
        }
        return passed ? new Status.Passed(this) : new Status.Failed(this, getFirstFailedStacktrace());
    }

    public boolean isBoundTo(RhinoContext context) {
        return this.context.equals(context);
    }

    public RhinoIt bind(RhinoContext context) {
        return new RhinoIt(this.spec, context);
    }

    public Throwable getFirstFailedStacktrace() {
        NativeArray resultItems = (NativeArray) context.executeFunction(getSpecResults(), "getItems");
        for (Object resultItemId : resultItems.getIds()) {
            NativeObject resultItem = (NativeObject) resultItems.get((Integer) resultItemId, resultItems);

            if (!((Boolean) context.executeFunction(resultItem, "passed"))) {
                return new JasmineSpecFailureException(resultItem);
            }
        }

        return null;
    }

    private NativeObject getSpecResults() {
        return (NativeObject) context.executeFunction(spec, "results");
    }

    public boolean isDone() {
        Object doneResult = spec.get("done", spec);
        return doneResult instanceof Boolean && ((Boolean) doneResult);
    }

    public void execute(){
        context.executeFunction(spec, "execute");
        while(!isDone()) { waitALittle(); }
    }

    public void execute(Notifier notifier){
        notifier.started(this);
        execute();
        getSpecResultStatus().notify(notifier);
    }

    @Override public String toString() {
        return new ToStringBuilder(this)
                .append("id", getId())
                .append("description", getDescription())
                .toString();
    }

    private void waitALittle() {
        try {
            Thread.sleep(SLEEP_TIME_MILISECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
