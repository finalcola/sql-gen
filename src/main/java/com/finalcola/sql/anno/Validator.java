package com.finalcola.sql.anno;

import com.finalcola.sql.config.Configuration;
import com.finalcola.sql.util.StringUtils;
import com.sun.tools.javac.comp.Check;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

/**
 * @author: yuanyou.
 * @date: 2019-11-18 15:42
 */
@Slf4j
public class Validator {

    private static final ConcurrentMap<Annotation, Class<? extends Checker>> CHECKER_CACHE = new ConcurrentHashMap<>();

    public static void passOrError(Object instance) {
        Class<?> klass = instance.getClass();
        Field[] fields = klass.getDeclaredFields();
        Map<Field, List<String>> errorMsgMap = new LinkedHashMap<>();
        for (Field field : fields) {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            Annotation[] annotations = field.getDeclaredAnnotations();
            if (annotations == null || annotations.length == 0) {
                continue;
            }
            for (Annotation annotation : annotations) {
                NeedCheck declaredAnnotation = annotation.annotationType().getDeclaredAnnotation(NeedCheck.class);
                if (declaredAnnotation == null) {
                    continue;
                }
                // check field
                Class<? extends Annotation> annoClass = annotation.annotationType();
                try {
                    Method checkerMethod = annoClass.getDeclaredMethod("checker");
                    Class<? extends Checker> checkerClass = (Class<? extends Checker>) checkerMethod.invoke(annotation);
                    Checker checker = checkerClass.newInstance();
                    boolean checkResult = checker.check(annotation, instance, field);
                    if (!checkResult) {
                        Method messageMethod = annoClass.getDeclaredMethod("message");
                        String msg = (String) messageMethod.invoke(annotation);
                        if (StringUtils.isBlank(msg)) {
                            msg = field.getName() + " can not be null";
                        }
                        List<String> errorMsgs = errorMsgMap.computeIfAbsent(field, k -> new ArrayList<>());
                        errorMsgs.add(msg);
                    }
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                    log.warn("反射异常", e);
                    throw new RuntimeException(e);
                }
            }
        }
        if (!errorMsgMap.isEmpty()) {
            String errorMsg = errorMsgMap.values().stream()
                    .flatMap(list -> Stream.of(list.toArray(new String[0])))
                    .reduce((s1, s2) -> s1 + ";" + s2)
                    .orElse("");
            throw new RuntimeException(errorMsg);
        }
    }
}
