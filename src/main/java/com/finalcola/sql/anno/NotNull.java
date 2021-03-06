package com.finalcola.sql.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: yuanyou.
 * @date: 2019-11-12 15:20
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@NeedCheck
public @interface NotNull {

    String message() default "";

    Class<? extends Checker> checker() default NotNullChecker.class;
}
