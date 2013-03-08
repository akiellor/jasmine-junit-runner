package jasmine.runtime;

import jasmine.rhino.RhinoContext;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import org.junit.runner.Description;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;

import static org.junit.Assert.assertTrue;

public class It {
    private static final int SLEEP_TIME_MILISECONDS = 50;

    private final Supplier<Description> description;
    private final RhinoContext context;
    private final NativeObject spec;

    It(final NativeObject spec, RhinoContext context) {
        this(spec, context, Suppliers.memoize(new Supplier<Description>() {
            @Override public Description get() {
                final String descriptionString = String.valueOf(spec.get("description", spec));
                return Description.createSuiteDescription(descriptionString, spec);
            }
        }));
    }

    private It(NativeObject spec, RhinoContext context, Supplier<Description> description) {
        this.spec = spec;
        this.context = context;
        this.description = description;
    }

    public Description getDescription() {
        return description.get();
    }

    public Status getSpecResultStatus() {
        assertTrue(isDone());

        NativeObject results = getSpecResults();
        boolean passed = (Boolean) context.executeFunction(results, "passed");
        boolean skipped = (Boolean) results.get("skipped", results);

        if (skipped) {
            return Status.SKIPPED;
        }
        return passed ? Status.PASSED : Status.FAILED;
    }

    public boolean isBoundTo(RhinoContext context) {
        return this.context.equals(context);
    }

    public It bind(RhinoContext context) {
        return new It(this.spec, context, this.description);
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
        getSpecResultStatus().notify(notifier, this);
    }

    @Override
    public String toString() {
        return description.get().getDisplayName();
    }

    private void waitALittle() {
        try {
            Thread.sleep(SLEEP_TIME_MILISECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
