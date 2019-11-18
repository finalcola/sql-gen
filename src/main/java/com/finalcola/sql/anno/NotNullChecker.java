package com.finalcola.sql.anno;

import java.lang.reflect.Field;

/**
 * @author: yuanyou.
 * @date: 2019-11-18 15:44
 */
public class NotNullChecker implements Checker<NotNull> {


    @Override
    public boolean check(NotNull annotation, Object instance, Field field) throws IllegalAccessException {
        Object value = field.get(instance);
        return value != null;
    }
}
