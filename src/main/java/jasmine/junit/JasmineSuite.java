package jasmine.junit;

import org.apache.commons.lang.StringUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface JasmineSuite {
    String[] specs() default {};

    String[] sources() default {};

    boolean debug() default false;

    boolean envJs() default false;

    String specRunnerSubDir() default StringUtils.EMPTY;
}
