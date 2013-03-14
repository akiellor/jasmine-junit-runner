package jasmine.runtime.rhino;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import jasmine.runtime.Describe;
import jasmine.runtime.JasmineVisitor;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.runner.Description;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class RhinoDescribe implements Describe {
    private final NativeObject object;
    private final RhinoContext context;
    private Description description;

    public RhinoDescribe(final NativeObject object, RhinoContext context){
        this.object = object;
        this.context = context;
    }

    public List<RhinoDescribe> getDescribes() {
        NativeArray suites = (NativeArray) context.executeFunction(object, "suites");
        List<RhinoDescribe> describes = newArrayList();
        for(Object id : suites.getIndexIds()){
            describes.add(new RhinoDescribe((NativeObject)suites.get(id), context));
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

        RhinoDescribe describe = (RhinoDescribe) o;

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

    @Override public String getId() {
        return String.valueOf(object.get("id", object));
    }

    @Override public String getDescription() {
        return String.valueOf(object.get("description", object));
    }

    @Override public Optional<Describe> getParent() {
        NativeObject parentSuite = (NativeObject) object.get("parentSuite", object);
        if(parentSuite == null){
            return Optional.absent();
        }else{
            return Optional.<Describe>of(new RhinoDescribe(parentSuite, context));
        }
    }

    public void accept(JasmineVisitor visitor) {
        visitor.visit(this);
        for(RhinoIt it : getIts()){
            it.accept(visitor);
        }
        for(RhinoDescribe describe : getDescribes()){
            describe.accept(visitor);
        }
    }

    @Override public String toString() {
        return new ToStringBuilder(this)
                .append("id", getId())
                .append("description", getDescription())
                .toString();
    }
}
