package jasmine.junit;

import com.google.common.base.Optional;
import jasmine.runtime.Describe;
import jasmine.runtime.It;
import jasmine.runtime.JasmineVisitor;
import org.junit.runner.Description;

import java.util.HashMap;
import java.util.Map;

public class DescriptionBuilder implements JasmineVisitor {
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
