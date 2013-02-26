package be.klak.jasmine;

import be.klak.rhino.RhinoContext;
import be.klak.rhino.RhinoRunnable;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;

import java.util.UUID;

import static junit.framework.Assert.assertTrue;

public class It {
    public enum JasmineSpecStatus {
        PASSED,
        FAILED,
        SKIPPED
    }

    private final Supplier<Description> description;
    private final RhinoContext context;
    private final NativeObject spec;

    It(final NativeObject spec, RhinoContext context) {
        this(spec, context, Suppliers.memoize(new Supplier<Description>() {
            @Override public Description get() {
                final String descriptionString = String.valueOf(spec.get("description", spec));
                return Description.createSuiteDescription(descriptionString, UUID.randomUUID());
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

    public NativeObject getSpec() {
        return spec;
    }

    public boolean isPassed(RhinoContext context) {
        return getSpecResultStatus(context) == JasmineSpecStatus.PASSED;
    }

    public boolean isFailed(RhinoContext context) {
        return getSpecResultStatus(context) == JasmineSpecStatus.FAILED;
    }

    public JasmineSpecStatus getSpecResultStatus(RhinoContext context) {
        assertTrue(isDone());

        NativeObject results = getSpecResults(context);
        boolean passed = (Boolean) context.executeFunction(results, "passed");
        boolean skipped = (Boolean) results.get("skipped", results);

        if (skipped) {
            return JasmineSpecStatus.SKIPPED;
        }
        return passed ? JasmineSpecStatus.PASSED : JasmineSpecStatus.FAILED;
    }

    public Failure getJunitFailure(RhinoContext context) {
        assertTrue(isFailed(context));
        return new Failure(description.get(), getFirstFailedStacktrace(context));
    }

    public boolean isBoundTo(RhinoContext context) {
        return this.context.equals(context);
    }

    public It bind(RhinoContext context) {
        return new It(this.spec, context, this.description);
    }

    private Throwable getFirstFailedStacktrace(RhinoContext context) {
        NativeArray resultItems = (NativeArray) context.executeFunction(getSpecResults(context), "getItems");
        for (Object resultItemId : resultItems.getIds()) {
            NativeObject resultItem = (NativeObject) resultItems.get((Integer) resultItemId, resultItems);

            if (!((Boolean) context.executeFunction(resultItem, "passed"))) {
                return new JasmineSpecFailureException(resultItem);
            }
        }

        return null;
    }

    private NativeObject getSpecResults(RhinoContext context) {
        return (NativeObject) context.executeFunction(spec, "results");
    }

    public boolean isDone() {
        Object doneResult = spec.get("done", spec);
        return doneResult instanceof Boolean && ((Boolean) doneResult);
    }

    public void execute(RhinoContext baseContext) {
        baseContext.runAsync(new RhinoRunnable() {

            @Override
            public void run(RhinoContext context) {
                context.executeFunction(spec, "execute");
            }
        });
    }

    @Override
    public String toString() {
        return description.get().getDisplayName();
    }
}
