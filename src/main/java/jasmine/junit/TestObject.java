package jasmine.junit;

import com.google.common.base.Optional;
import jasmine.runtime.utils.Exceptions;
import org.apache.commons.lang.StringUtils;
import org.junit.runner.Description;

class TestObject {
    private final Object instance;
    private final Description description;

    public TestObject(Class<?> testClass) {
        try {
            this.instance = testClass.newInstance();
            this.description = Description.createSuiteDescription(testClass);
        } catch (Exception e) {
            throw Exceptions.unchecked(e);
        }
    }

    public Optional<JasmineSuite> getAnnotation() {
        return Optional.fromNullable(instance.getClass().getAnnotation(JasmineSuite.class));
    }

    public String getName() {
        return instance.getClass().getSimpleName();
    }

    public String getDefaultSpecPath() {
        return instance.getClass().getPackage().getName().replace(".", "/") + "/" + StringUtils.uncapitalize(instance.getClass().getSimpleName()).replaceAll("Test$", "Spec.js");
    }

    public Description getDescription() {
        return description;
    }
}
