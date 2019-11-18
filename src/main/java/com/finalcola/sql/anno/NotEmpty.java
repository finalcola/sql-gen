package com.finalcola.sql.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: yuanyou.
 * @date: 2019-11-18 17:36
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@NeedCheck
public @interface NotEmpty {

    String message() default "";

    Class<? extends Checker> checker() default NotEmptyChecker.class;

}
