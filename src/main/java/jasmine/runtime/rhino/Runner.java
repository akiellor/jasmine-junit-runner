package jasmine.runtime.rhino;

import jasmine.rhino.RhinoContext;
import jasmine.runtime.Notifier;
import jasmine.utils.Futures;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import org.junit.runner.Description;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.google.common.collect.Lists.newArrayList;

public class Runner {
    private final NativeObject object;
    private final RhinoContext context;
    private final Supplier<Description> description;
    private final ExecutorService executor;

    public Runner(NativeObject object, RhinoContext context, final Description description) {
        this.object = object;
        this.context = context;
        this.executor = Executors.newFixedThreadPool(10);
        this.description = Suppliers.memoize(new Supplier<Description>() {
            @Override public Description get() {
                for(RhinoDescribe rhinoDescribe : getDescribes()){
                    description.addChild(rhinoDescribe.getDescription());
                }
                return description;
            }
        });
    }

    public List<RhinoDescribe> getDescribes(){
        List<RhinoDescribe> allRhinoDescribes = getAllDescribes();
        allRhinoDescribes.removeAll(getChildDescribes());
        return allRhinoDescribes;
    }

    public Description getDescription(){
        return description.get();
    }

    public void execute(final Notifier notifier){
        List<RhinoIt> rhinoIts = getAllIts();
        if(rhinoIts.isEmpty()) { notifier.nothingToRun(); return; }

        Futures.await(Collections2.transform(rhinoIts, new Function<RhinoIt, Future<RhinoIt>>() {
            @Override public Future<RhinoIt> apply(final RhinoIt spec) {
                return executor.submit(new Callable<RhinoIt>() {
                    @Override public RhinoIt call() throws Exception {
                        RhinoContext fork = context.fork();

                        spec.bind(fork).execute(notifier);

                        return spec;
                    }
                });
            }
        }));

    }

    public List<RhinoIt> getAllIts() {
        return newArrayList(Iterables.concat(Collections2.transform(getDescribes(), new Function<RhinoDescribe, List<RhinoIt>>() {
            @Override public List<RhinoIt> apply(RhinoDescribe input) {
                return input.getAllIts();
            }
        })));
    }

    private List<RhinoDescribe> getChildDescribes(){
        return newArrayList(Iterables.concat(Iterables.transform(getAllDescribes(), new Function<RhinoDescribe, List<RhinoDescribe>>() {
            @Override public List<RhinoDescribe> apply(RhinoDescribe input) {
                return input.getDescribes();
            }
        })));
    }

    private List<RhinoDescribe> getAllDescribes() {
        NativeArray suites = (NativeArray) context.executeFunction(object, "suites");
        List<RhinoDescribe> rhinoDescribes = newArrayList();
        for(Object id : suites.getIndexIds()){
            rhinoDescribes.add(new RhinoDescribe((NativeObject)suites.get(id), context));
        }
        return rhinoDescribes;
    }
}
