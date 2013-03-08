package jasmine.junit;

import org.apache.commons.lang.StringUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface JasmineSuite {
    String[] specs() default {};

    String[] sources() default {};

    boolean generateSpecRunner() default false;

    boolean debug() default false;

    /**
     * If set to false, does not load EnvJS support.
     */
    boolean envJs() default true;

    String specRunnerSubDir() default StringUtils.EMPTY;
}
