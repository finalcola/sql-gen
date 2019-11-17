package com.finalcola.sql.struct;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: yuanyou.
 * @date: 2019-11-11 19:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColumnMeta {
    private String tableCat;
    private String tableSchemaName;
    private String tableName;
    private String columnName;
    private int dataType;
    private String dataTypeName;
    private int columnSize;
    private int decimalDigits;
    private int numPrecRadix;
    private int nullAble;
    private String remarks;
    private String columnDef;
    private int sqlDataType;
    private int sqlDatetimeSub;
    private int charOctetLength;
    private int ordinalPosition;
    private String isNullAble;
    private String isAutoincrement;
}
