package com.finalcola.sql.struct;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.*;

/**
 * @author: yuanyou.
 * @date: 2019-11-14 14:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ClassInfo {

    /**
     * 类名
     */
    private String name;

    /**
     * package
     */
    private String pkg;

    /**
     * 是否是接口
     */
    private boolean isInterface;

    /**
     * 访问修饰符
     */
    private String accessor = "public";

    /**
     * 父类
     */
    private String superClass;

    /**
     * 实现的接口
     */
    // TODO: 2019-11-14 需要考虑类名相同时的冲突情况
    private Set<String> impls = new HashSet<>();

    /**
     * 注解
     */
    private Set<String> annotations = new HashSet<>();

    /**
     * 注释
     */
    private String doc;

    /**
     * import
     */
    private Set<String> imports = new HashSet<>();

    /**
     * 字段列表
     */
    private List<FieldInfo> fieldInfos = new LinkedList<>();

    /**
     * 方法列表
     */
    private List<MethodInfo> methodInfos = new LinkedList<>();
}
