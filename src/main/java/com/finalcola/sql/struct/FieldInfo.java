package com.finalcola.sql.struct;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author: yuanyou.
 * @date: 2019-11-14 14:13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class FieldInfo {

    /**
     * 字段名
     */
    private String name;

    /**
     * 注释
     */
    private String doc;

    /**
     * 字段类型
     */
    private String type;

    /**
     * 注解
     */
    private Set<String> annotations = new HashSet<>();

    /**
     * 访问权限
     */
    private String accessor = "private";
}
