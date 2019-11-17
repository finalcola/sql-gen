package com.finalcola.sql.process.node;

import com.finalcola.sql.config.Configuration;
import com.finalcola.sql.process.AbstractNodeProcessor;
import com.finalcola.sql.process.SqlContext;
import com.finalcola.sql.struct.*;
import com.finalcola.sql.util.MysqlKeywordUtils;
import lombok.EqualsAndHashCode;
import org.dom4j.Element;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author: yuanyou.
 * @date: 2019-11-12 19:25
 */
@EqualsAndHashCode(callSuper = false)
public class InsertBatchProcessor extends InsertProcessor {
    @Override
    public String getType() {
        return "insertBatch";
    }

    @Override
    protected String getSqlId() {
        return "insertBatch";
    }

    @Override
    protected String createInsertSql(TableMeta tableMeta, Configuration configuration) {
        String tableName = MysqlKeywordUtils.processKeyword(tableMeta.getTableName());
        Map<String, ColumnMeta> primaryKeyMap = tableMeta.getPrimaryKeyMap();
        Map<String, ColumnMeta> allColumns = tableMeta.getAllColumns();

        StringBuilder fieldsBuilder = new StringBuilder();
        StringBuilder paramsBuilder = new StringBuilder();
        for (Map.Entry<String, ColumnMeta> entry : primaryKeyMap.entrySet()) {
            String primaryKeyName = entry.getKey();
            ColumnMeta columnMeta = entry.getValue();
            if ("YES".equalsIgnoreCase(columnMeta.getIsAutoincrement())) {
                continue;
            }
            fieldsBuilder.append(MysqlKeywordUtils.processKeyword(primaryKeyName)).append(",");
            paramsBuilder.append("#{").append(columnToCamel(primaryKeyName, configuration)).append("},");
        }
        for (Map.Entry<String, ColumnMeta> entry : allColumns.entrySet()) {
            String column = entry.getKey();
            fieldsBuilder.append(MysqlKeywordUtils.processKeyword(column)).append(",");
            paramsBuilder.append("#{item.").append(columnToCamel(column, configuration)).append("},");
        }
        deleteLastChar(fieldsBuilder);
        deleteLastChar(paramsBuilder);

        String sqlFormat = "insert into (%s) %s values <foreach collection=\"list\" item=\"item\" separator=\",\">(%s)</foreach>";
        return String.format(sqlFormat, fieldsBuilder.toString(), tableName, paramsBuilder.toString());
    }

    @Override
    protected String getMethodDoc() {
        return "批量新增";
    }

    @Override
    protected List<ParamInfo> getParamInfos(ClassInfo mapperClassInfo, SqlContext sqlContext) {
        mapperClassInfo.getImports().add(getFullEntityName(sqlContext));
        mapperClassInfo.getImports().add("java.util.List");
        ParamInfo paramInfo = new ParamInfo();
        String entityClassName = String.format("List<%s>", getEntityName(sqlContext));
        paramInfo.setType(entityClassName);
        paramInfo.setName("list");
        return Arrays.asList(paramInfo);
    }

}
