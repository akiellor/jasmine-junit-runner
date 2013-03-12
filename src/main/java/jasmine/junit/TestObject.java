package jasmine.junit;

import com.google.common.base.Optional;
import jasmine.runtime.Backend;
import jasmine.utils.Exceptions;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class TestObject {
    private final Object instance;

    public TestObject(Class<?> testClass){
        try {
            this.instance = testClass.newInstance();
        } catch (Exception e) {
            throw Exceptions.unchecked(e);
        }
    }

    public void befores(Backend context){
        fireMethodsWithSpecifiedAnnotationIfAny(context, Before.class);
    }

    public void afters(Backend context){
        fireMethodsWithSpecifiedAnnotationIfAny(context, After.class);
    }

    public Optional<JasmineSuite> getAnnotation(){
        return Optional.fromNullable(instance.getClass().getAnnotation(JasmineSuite.class));
    }

    private void fireMethodsWithSpecifiedAnnotationIfAny(Backend fork, Class<? extends Annotation> annotation) {
        for (Method method : instance.getClass().getMethods()) {

            try {
                if (method.getAnnotation(annotation) != null) {
                    method.setAccessible(true);
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length == 0) {
                        method.invoke(instance, (Object[]) null);
                    } else if (parameterTypes.length == 1 && Backend.class.isAssignableFrom(parameterTypes[0])) {
                        method.invoke(instance, fork);
                    } else {
                        throw new IllegalStateException("Annotated method does not have zero or rhinoContext as parameterTypes");
                    }
                }
            } catch (Exception ex) {
                throw new RuntimeException(
                        "Exception while firing " + annotation.getSimpleName() + " method: " + method.getName(), ex);
            }
        }
    }

    public String getName() {
        return instance.getClass().getSimpleName();
    }

    public String getDefaultSpecPath() {
        return instance.getClass().getPackage().getName().replace(".", "/") + "/" + StringUtils.uncapitalize(instance.getClass().getSimpleName()).replaceAll("Test$", "Spec.js");
    }
}