package jasmine.runtime;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import jasmine.rhino.RhinoContext;
import jasmine.runtime.rhino.RhinoDescribe;
import jasmine.runtime.rhino.RhinoIt;
import jasmine.utils.Futures;
import org.junit.runner.Description;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.google.common.collect.Lists.newArrayList;

public class Runner {
    private final NativeObject object;
    private final RhinoContext context;
    private final Description root;
    private final ExecutorService executor;
    private boolean descriptionInitialized = false;

    public Runner(NativeObject object, RhinoContext context, final Description root) {
        this.object = object;
        this.context = context;
        this.root = root;
        this.executor = Executors.newFixedThreadPool(10);
    }

    public List<RhinoDescribe> getDescribes(){
        List<RhinoDescribe> allDescribes = getAllDescribes();
        allDescribes.removeAll(getChildDescribes());
        return allDescribes;
    }

    private static class DescriptionBuilder implements JasmineVisitor {
        private final Map<String, Description> descriptions;

        public DescriptionBuilder(Description root){
            descriptions = new HashMap<String, Description>();
            descriptions.put("ROOT", root);
        }

        @Override public void visit(Describe describe) {
            Description description = Description.createSuiteDescription(describe.getStringDescription(), describe.getId());
            descriptions.put(describe.getId(), description);

            Optional<Describe> parent = describe.getParent();
            if(parent.isPresent()){
                descriptions.get(parent.get().getId()).addChild(description);
            }else{
                descriptions.get("ROOT").addChild(description);
            }
        }

        @Override public void visit(It it) {
            Description description = Description.createSuiteDescription(it.getStringDescription(), it.getId());
            descriptions.get(it.getParent().getId()).addChild(description);
        }
    }

    public Description getDescription(){
        if(!descriptionInitialized){
            DescriptionBuilder builder = new DescriptionBuilder(root);
            for(RhinoDescribe describe : getDescribes()){
                describe.accept(builder);
            }
            descriptionInitialized = true;
        }
        return root;
    }

    public void execute(final Notifier notifier){
        List<RhinoIt> its = getAllIts();
        if(its.isEmpty()) { notifier.nothingToRun(); return; }

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
