package jasmine.junit;

import com.google.common.base.Optional;
import jasmine.runtime.Address;
import jasmine.runtime.Describe;
import jasmine.runtime.It;
import jasmine.runtime.JasmineVisitor;
import org.junit.runner.Description;

import java.util.HashMap;
import java.util.Map;

class DescriptionBuilder implements JasmineVisitor {
    private final Map<String, Description> descriptions;
    private final Address<Describe> address;

    public DescriptionBuilder(Description root){
        this.descriptions = new HashMap<String, Description>();
        this.descriptions.put("ROOT", root);
        this.address = new Address<Describe>();
    }

    private DescriptionBuilder(Map<String, Description> descriptions, Address<Describe> address){
        this.descriptions = descriptions;
        this.address = address;
    }

    @Override public void visit(Describe describe) {
        Description description = Description.createSuiteDescription(describe.getDescription(), describe.getId());
        descriptions.put(describe.getId(), description);

        Optional<Describe> parent = getCurrentAddress().peek();
        if(parent.isPresent()){
            descriptions.get(parent.get().getId()).addChild(description);
        }else{
            descriptions.get("ROOT").addChild(description);
        }
    }

    @Override public void visit(It it) {
        Description description = Description.createSuiteDescription(it.getDescription(), it.getId());
        descriptions.get(getCurrentAddress().peek().get().getId()).addChild(description);
    }

    @Override public JasmineVisitor forNextLevel(Describe describe) {
        return new DescriptionBuilder(descriptions, address.push(describe));
    }

    @Override public Address<Describe> getCurrentAddress() {
        return address;
    }
}
