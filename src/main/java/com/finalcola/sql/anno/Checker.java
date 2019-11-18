package com.finalcola.sql.anno;

import java.lang.reflect.Field;

/**
 * @author: yuanyou.
 * @date: 2019-11-18 15:43
 */
public interface Checker<Anno> {

    boolean check(Anno annotation, Object instance, Field field) throws IllegalAccessException;
}
