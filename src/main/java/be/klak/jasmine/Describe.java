package be.klak.jasmine;

import be.klak.rhino.RhinoContext;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class Describe {
    private final NativeObject object;
    private final RhinoContext context;

    public Describe(NativeObject object, RhinoContext context){
        this.object = object;
        this.context = context;
    }

    public String getDescription(){
        return String.valueOf(object.get("description"));
    }

    public Set<Describe> getDescribes() {
        NativeArray suites = (NativeArray) context.executeFunction(object, "suites");
        Set<Describe> describes = newHashSet();
        for(Object id : suites.getIndexIds()){
            describes.add(new Describe((NativeObject)suites.get(id), context));
        }
        return describes;
    }

    public Set<It> getIts() {
        NativeArray suites = (NativeArray) context.executeFunction(object, "specs");
        Set<It> its = newHashSet();
        for(Object id : suites.getIndexIds()){
            its.add(new It((NativeObject) suites.get(id)));
        }
        return its;
    }

    public boolean isBoundTo(RhinoContext context) {
        return this.context.equals(context);
    }

    public Describe bind(RhinoContext newContext) {
        return new Describe(object, newContext);
    }

    public Set<It> getAllIts() {
        Iterable<It> allChildren = Iterables.concat(Collections2.transform(getDescribes(), new Function<Describe, Set<It>>() {
            @Override public Set<It> apply(Describe input) {
                return input.getAllIts();
            }
        }));

        return Sets.newHashSet(Iterables.concat(allChildren, getIts()));
    }
}
