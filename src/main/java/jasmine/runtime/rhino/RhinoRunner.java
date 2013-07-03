package jasmine.runtime.rhino;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import jasmine.runtime.JasmineVisitor;
import jasmine.runtime.Notifier;
import jasmine.runtime.utils.Exceptions;
import jasmine.runtime.utils.Futures;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;

import java.util.List;
import java.util.concurrent.*;

import static com.google.common.collect.Lists.newArrayList;

public class RhinoRunner {
    private final NativeObject object;
    private final RhinoContext context;
    private final ExecutorService executor;

    public RhinoRunner(NativeObject object, RhinoContext context) {
        this(object, context, Executors.newFixedThreadPool(10));
    }

    RhinoRunner(NativeObject object, RhinoContext context, ExecutorService executor) {
        this.object = object;
        this.context = context;
        this.executor = executor;
    }

    public List<RhinoDescribe> getDescribes(){
        List<RhinoDescribe> allDescribes = getAllDescribes();
        allDescribes.removeAll(getChildDescribes());
        return allDescribes;
    }

    public void accept(JasmineVisitor visitor){
        for(RhinoDescribe describe : getDescribes()){
            describe.accept(visitor);
        }
    }

    public void execute(final Notifier notifier){
        List<RhinoIt> its = getAllIts();

        Futures.await(Collections2.transform(its, new Function<RhinoIt, Future<RhinoIt>>() {
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

        try {
            executor.awaitTermination(1L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw Exceptions.unchecked(e);
        }
        executor.shutdown();

        notifier.finished();
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
        List<RhinoDescribe> describes = newArrayList();
        for(Object id : suites.getIndexIds()){
            describes.add(new RhinoDescribe((NativeObject)suites.get(id), context));
        }
        return describes;
    }
}
