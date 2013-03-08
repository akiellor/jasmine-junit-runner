package jasmine.runtime.junit;

import jasmine.runtime.rhino.RhinoContext;
import jasmine.runtime.utils.Exceptions;
import com.google.common.base.Optional;
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

    public void befores(RhinoContext context){
        fireMethodsWithSpecifiedAnnotationIfAny(context, Before.class);
    }

    public void afters(RhinoContext context){
        fireMethodsWithSpecifiedAnnotationIfAny(context, After.class);
    }

    public Optional<JasmineSuite> getAnnotation(){
        return Optional.fromNullable(instance.getClass().getAnnotation(JasmineSuite.class));
    }

    private void fireMethodsWithSpecifiedAnnotationIfAny(RhinoContext fork, Class<? extends Annotation> annotation) {
        for (Method method : instance.getClass().getMethods()) {

            try {
                if (method.getAnnotation(annotation) != null) {
                    method.setAccessible(true);
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length == 0) {
                        method.invoke(instance, (Object[]) null);
                    } else if (parameterTypes.length == 1 && RhinoContext.class.isAssignableFrom(parameterTypes[0])) {
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
}
