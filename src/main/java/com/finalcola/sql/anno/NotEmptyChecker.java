package com.finalcola.sql.anno;

import com.finalcola.sql.util.StringUtils;

import java.lang.reflect.Field;

/**
 * @author: yuanyou.
 * @date: 2019-11-18 17:37
 */
public class NotEmptyChecker implements Checker<NotEmptyChecker> {
    @Override
    public boolean check(NotEmptyChecker annotation, Object instance, Field field) throws IllegalAccessException {
        Object value = field.get(instance);
        if (value instanceof String && StringUtils.isBlank((String) value)) {
            return false;
        }
        return true;
    }
}
