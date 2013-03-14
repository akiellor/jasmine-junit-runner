package jasmine.runtime;

import jasmine.rhino.RhinoContext;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import jasmine.runtime.rhino.RhinoIt;
import org.junit.runner.Description;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class Describe {
    private final NativeObject object;
    private final RhinoContext context;
    private Supplier<Description> description;

    public Describe(final NativeObject object, RhinoContext context){
        this.object = object;
        this.context = context;

        this.description = Suppliers.memoize(new Supplier<Description>() {
            @Override public Description get() {
                Description description = Description.createSuiteDescription(String.valueOf(object.get("description")), object);
                for(RhinoIt it : getIts()){
                    description.addChild(it.getDescription());
                }
                for(Describe describe : getDescribes()){
                    description.addChild(describe.getDescription());
                }
                return description;
            }
        });
    }

    public Description getDescription(){
        return description.get();
    }

    public List<Describe> getDescribes() {
        NativeArray suites = (NativeArray) context.executeFunction(object, "suites");
        List<Describe> describes = newArrayList();
        for(Object id : suites.getIndexIds()){
            describes.add(new Describe((NativeObject)suites.get(id), context));
        }
        return describes;
    }

    public List<RhinoIt> getIts() {
        NativeArray suites = (NativeArray) context.executeFunction(object, "specs");
        List<RhinoIt> its = newArrayList();
        for(Object id : suites.getIndexIds()){
            its.add(new RhinoIt((NativeObject) suites.get(id), context));
        }
        return its;
    }

    public boolean isBoundTo(RhinoContext context) {
        return this.context.equals(context);
    }

    public Describe bind(RhinoContext newContext) {
        return new Describe(object, newContext);
    }

    public List<RhinoIt> getAllIts() {
        Iterable<RhinoIt> allChildren = Iterables.concat(Collections2.transform(getDescribes(), new Function<Describe, List<RhinoIt>>() {
            @Override public List<RhinoIt> apply(Describe input) {
                return input.getAllIts();
            }
        }));

        return Lists.newArrayList(Iterables.concat(allChildren, getIts()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Describe describe = (Describe) o;

        if (context != null ? !context.equals(describe.context) : describe.context != null) return false;
        if (object != null ? !object.equals(describe.object) : describe.object != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = object != null ? object.hashCode() : 0;
        result = 31 * result + (context != null ? context.hashCode() : 0);
        return result;
    }
}
