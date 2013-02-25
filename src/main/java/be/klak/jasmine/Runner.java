package be.klak.jasmine;

import be.klak.rhino.RhinoContext;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import org.junit.runner.Description;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;

import javax.annotation.Nullable;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class Runner {
    private final NativeObject object;
    private final RhinoContext context;
    private final Supplier<Description> description;

    public Runner(NativeObject object, RhinoContext context, final Description description) {
        this.object = object;
        this.context = context;
        this.description = Suppliers.memoize(new Supplier<Description>() {
            @Override public Description get() {
                for(Describe describe : getDescribes()){
                    description.addChild(describe.getDescription());
                }
                return description;
            }
        });
    }

    public Set<Describe> getDescribes() {
        NativeArray suites = (NativeArray) context.executeFunction(object, "suites");
        Set<Describe> describes = newHashSet();
        for(Object id : suites.getIndexIds()){
            describes.add(new Describe((NativeObject)suites.get(id), context));
        }
        return describes;
    }

    public Description getDescription(){
        return description.get();
    }

    public Set<It> getAllIts() {
        return newHashSet(Iterables.concat(Collections2.transform(getDescribes(), new Function<Describe, Set<It>>() {
            @Override public Set<It> apply(@Nullable Describe input) {
                return input.getAllIts();
            }
        })));
    }
}
