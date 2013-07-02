package jasmine.junit;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface JasmineSuite {
    String[] specs() default {};

    String[] sources() default {};
}
