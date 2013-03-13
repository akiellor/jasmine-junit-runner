package jasmine.runtime.rhino;

import jasmine.rhino.RhinoContext;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.junit.runner.Description;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class RhinoDescribe {
    private final NativeObject object;
    private final RhinoContext context;
    private Supplier<Description> description;

    public RhinoDescribe(final NativeObject object, RhinoContext context){
        this.object = object;
        this.context = context;

        this.description = Suppliers.memoize(new Supplier<Description>() {
            @Override public Description get() {
                Description description = Description.createSuiteDescription(String.valueOf(object.get("description")), object);
                for(RhinoIt rhinoIt : getIts()){
                    description.addChild(rhinoIt.getDescription());
                }
                for(RhinoDescribe rhinoDescribe : getDescribes()){
                    description.addChild(rhinoDescribe.getDescription());
                }
                return description;
            }
        });
    }

    public Description getDescription(){
        return description.get();
    }

    public List<RhinoDescribe> getDescribes() {
        NativeArray suites = (NativeArray) context.executeFunction(object, "suites");
        List<RhinoDescribe> rhinoDescribes = newArrayList();
        for(Object id : suites.getIndexIds()){
            rhinoDescribes.add(new RhinoDescribe((NativeObject)suites.get(id), context));
        }
        return rhinoDescribes;
    }

    public List<RhinoIt> getIts() {
        NativeArray suites = (NativeArray) context.executeFunction(object, "specs");
        List<RhinoIt> rhinoIts = newArrayList();
        for(Object id : suites.getIndexIds()){
            rhinoIts.add(new RhinoIt((NativeObject) suites.get(id), context));
        }
        return rhinoIts;
    }

    public boolean isBoundTo(RhinoContext context) {
        return this.context.equals(context);
    }

    public RhinoDescribe bind(RhinoContext newContext) {
        return new RhinoDescribe(object, newContext);
    }

    public List<RhinoIt> getAllIts() {
        Iterable<RhinoIt> allChildren = Iterables.concat(Collections2.transform(getDescribes(), new Function<RhinoDescribe, List<RhinoIt>>() {
            @Override public List<RhinoIt> apply(RhinoDescribe input) {
                return input.getAllIts();
            }
        }));

        return Lists.newArrayList(Iterables.concat(allChildren, getIts()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RhinoDescribe rhinoDescribe = (RhinoDescribe) o;

        if (context != null ? !context.equals(rhinoDescribe.context) : rhinoDescribe.context != null) return false;
        if (object != null ? !object.equals(rhinoDescribe.object) : rhinoDescribe.object != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = object != null ? object.hashCode() : 0;
        result = 31 * result + (context != null ? context.hashCode() : 0);
        return result;
    }
}
