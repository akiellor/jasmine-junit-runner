package jasmine.runtime.webdriver;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.runner.Description;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;
import java.util.Stack;

import static com.google.common.collect.Sets.newHashSet;

public class RunnerHandler implements HttpHandler {
    private final Description rootDescription;

    public RunnerHandler(Description rootDescription) {
        this.rootDescription = rootDescription;
    }

    @Override public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control) throws Exception {
        HttpRunner runner = parsePlan(new JsonParser().parse(request.body()));

        runner.accept(new DescriptionBuilderVisitor(rootDescription));
        response.status(200).end();
    }

    private static class DescriptionBuilderVisitor implements Visitor{
        private Stack<Description> descriptions;

        public DescriptionBuilderVisitor(Description root){
            descriptions = new Stack<Description>();
            descriptions.push(root);
        }

        @Override
        public void visit(HttpSuite suite){
            Description suiteDescription = Description.createSuiteDescription(suite.getName(), suite.getId());
            while(descriptions.size() != 1){
                descriptions.pop();
            }
            descriptions.peek().addChild(suiteDescription);
            descriptions.push(suiteDescription);
        }

        @Override public void visit(HttpSpec spec) {
            descriptions.peek().addChild(Description.createSuiteDescription(spec.getName(), spec.getId()));
        }
    }

    private HttpRunner parsePlan(JsonElement element){
        JsonElement suites = element.getAsJsonObject().get("suites");
        return new HttpRunner(newHashSet(Iterables.transform(suites.getAsJsonArray(), new Function<JsonElement, HttpSuite>() {
            @Override public HttpSuite apply(@Nullable JsonElement suite) {
                return parseSuite(suite);
            }
        })));
    }

    private HttpSuite parseSuite(JsonElement element) {
        JsonObject suite = element.getAsJsonObject();
        String id = suite.get("id").getAsString();
        JsonElement parentSuiteIdElement = suite.get("parentSuiteId");
        String parentSuiteId = parentSuiteIdElement.isJsonNull() ? null : parentSuiteIdElement.getAsString();
        String name = suite.get("name").getAsString();
        Set<HttpSpec> specs = newHashSet(Iterables.transform(suite.get("specs").getAsJsonArray(), new Function<JsonElement, HttpSpec>() {
            @Override public HttpSpec apply(@Nullable JsonElement spec) {
                return parseSpec(spec);
            }
        }));
        return new HttpSuite(id, parentSuiteId, name, specs);
    }

    private HttpSpec parseSpec(JsonElement element) {
        JsonObject object = element.getAsJsonObject();
        return new HttpSpec(object.get("id").getAsString(), object.get("name").getAsString());
    }

    public static class HttpSpec {
        private final String id;
        private final String name;

        public HttpSpec(String id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override public String toString() {
            return "HttpSpec{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }

        public void accept(Visitor visitor) {
            visitor.visit(this);
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public static class HttpRunner {
        private final Set<HttpSuite> suites;

        public HttpRunner(Set<HttpSuite> suites) {
            this.suites = suites;
        }

        @Override public String toString() {
            return "HttpRunner{" +
                    "suites=" + suites +
                    '}';
        }

        public void accept(Visitor visitor) {
            for(HttpSuite suite : suites){
                suite.accept(visitor);
            }
        }
    }

    public static class HttpSuite {
        private final String id;
        private final String parentSuiteId;
        private final String name;
        private final Set<HttpSpec> specs;

        public HttpSuite(String id, String parentSuiteId, String name, Set<HttpSpec> specs) {
            this.id = id;
            this.parentSuiteId = parentSuiteId;
            this.name = name;
            this.specs = Collections.unmodifiableSet(specs);
        }

        @Override public String toString() {
            return "HttpSuite{" +
                    "id='" + id + '\'' +
                    ", parentSuiteId='" + parentSuiteId + '\'' +
                    ", name='" + name + '\'' +
                    ", specs=" + specs +
                    '}';
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

        public void accept(Visitor visitor) {
            visitor.visit(this);
            for(HttpSpec spec : specs){
                spec.accept(visitor);
            }
        }
    }
}
