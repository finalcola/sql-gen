package com.finalcola.sql.struct;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: yuanyou.
 * @date: 2019-11-11 19:44
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndexMeta {
    private List<ColumnMeta> values = new ArrayList<ColumnMeta>();

    private boolean nonUnique;
    private String indexQualifier;
    private String indexName;
    private short type;
    private IndexType indextype;
    private String ascOrDesc;
    private int cardinality;
    private int ordinalPosition;
}
