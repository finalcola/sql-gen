package com.finalcola.sql.struct;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.*;

/**
 * @author: yuanyou.
 * @date: 2019-11-14 14:22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class MethodInfo {

    /**
     * 方法名
     */
    private String name;

    /**
     * 注释
     */
    private String doc;

    /**
     * 返回值类型
     */
    private String returnType;

    /**
     * 参数信息
     */
    private List<ParamInfo> paramInfos = new LinkedList<>();

    /**
     * 是否支持泛型
     */
    private Boolean isGeneric;

    /**
     * 访问权限
     */
    private String accessor;

    /**
     * 方法抛出的异常
     */
    private Set<String> exceptions = new HashSet<>(4);

    /**
     * 注解
     */
    private Set<String> annotaions = new HashSet<>(4);

    /**
     * 代码
     */
    private List<String> codes = new LinkedList<>();

    /**
     * 抽象类方法
     */
    private Boolean isAbstract;

    /**
     * 接口方法
     */
    private Boolean isInterface;
}
