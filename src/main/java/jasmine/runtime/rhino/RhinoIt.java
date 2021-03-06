package jasmine.runtime.rhino;

import jasmine.runtime.Failure;
import jasmine.runtime.It;
import jasmine.runtime.JasmineVisitor;
import jasmine.runtime.Notifier;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptableObject;

import static org.junit.Assert.assertTrue;

class RhinoIt implements It {
    private static final int SLEEP_TIME_MILLISECONDS = 50;

    private final RhinoContext context;
    private final NativeObject spec;

    public RhinoIt(NativeObject spec, RhinoContext context) {
        this.spec = spec;
        this.context = context;
    }

    @Override
    public String getId() {
        NativeObject parentSuite = (NativeObject) spec.get("suite", spec);
        String suiteId = String.valueOf(parentSuite.get("id", parentSuite));
        String specId = String.valueOf(spec.get("id", spec));
        return suiteId + "-" + specId;
    }

    @Override
    public String getDescription() {
        return String.valueOf(spec.get("description", spec));
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
        return passed ? new Status.Passed(this) : new Status.Failed(this, getFailure());
    }

    public boolean isBoundTo(RhinoContext context) {
        return this.context.equals(context);
    }

    public RhinoIt bind(RhinoContext context) {
        return new RhinoIt(this.spec, context);
    }

    private Failure getFailure() {
        NativeArray resultItems = (NativeArray) context.executeFunction(getSpecResults(), "getItems");
        for (Object resultItemId : resultItems.getIds()) {
            final NativeObject resultItem = (NativeObject) resultItems.get((Integer) resultItemId, resultItems);

            if (!((Boolean) context.executeFunction(resultItem, "passed"))) {
                return new Failure() {
                    @Override
                    public String getMessage() {
                        return String.valueOf(resultItem.get("message"));
                    }

                    @Override
                    public String getStack() {
                        return String.valueOf(((ScriptableObject) resultItem.get("trace")).get("stack"));
                    }
                };
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

    public void execute() {
        context.executeFunction(spec, "execute");
        while (!isDone()) {
            waitALittle();
        }
    }

    public void execute(Notifier notifier) {
        notifier.started(this);
        execute();
        getSpecResultStatus().notify(notifier);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", getId())
                .append("description", getDescription())
                .toString();
    }

    private void waitALittle() {
        try {
            Thread.sleep(SLEEP_TIME_MILLISECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
