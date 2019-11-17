package com.finalcola.sql.struct;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author: yuanyou.
 * @date: 2019-11-14 14:23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ParamInfo {

    private String type;

    private String name;

    private Set<String> annotaions = new HashSet<>(2);

}
