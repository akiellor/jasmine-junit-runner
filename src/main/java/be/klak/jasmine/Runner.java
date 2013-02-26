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

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

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

    public List<Describe> getDescribes(){
        List<Describe> allDescribes = getAllDescribes();
        allDescribes.removeAll(getChildDescribes());
        return allDescribes;
    }

    public Description getDescription(){
        return description.get();
    }

    public List<It> getAllIts() {
        return newArrayList(Iterables.concat(Collections2.transform(getDescribes(), new Function<Describe, List<It>>() {
            @Override public List<It> apply(Describe input) {
                return input.getAllIts();
            }
        })));
    }

    private List<Describe> getChildDescribes(){
        return newArrayList(Iterables.concat(Iterables.transform(getAllDescribes(), new Function<Describe, List<Describe>>() {
            @Override public List<Describe> apply(Describe input) {
                return input.getDescribes();
            }
        })));
    }

    private List<Describe> getAllDescribes() {
        NativeArray suites = (NativeArray) context.executeFunction(object, "suites");
        List<Describe> describes = newArrayList();
        for(Object id : suites.getIndexIds()){
            describes.add(new Describe((NativeObject)suites.get(id), context));
        }
        return describes;
    }
}
