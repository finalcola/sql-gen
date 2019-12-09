package com.finalcola.sql.process.node;

import com.finalcola.sql.anno.ServiceImpl;
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
 * @date: 2019-11-12 18:03
 */
@EqualsAndHashCode(callSuper = false)
@ServiceImpl(name = "insert")
public class InsertProcessor extends AbstractNodeProcessor {
    @Override
    public String getType() {
        return "insert";
    }

    @Override
    public int getOrder() {
        return super.getOrder() + 1;
    }

    @Override
    protected SqlContext createSubSqlContext(SqlContext parentContext) {
        Element node = createInsert(parentContext);
        SqlContext sqlContext = new SqlContext();
        sqlContext.setNode(node);
        return sqlContext;
    }

    private Element createInsert(SqlContext parentContext) {
        Element element = createElement("insert");
        element.addAttribute("id", getSqlId());
        TableMeta tableMeta = parentContext.getTableMeta();
        boolean useGeneratedKeys = parentContext.getConfiguration().isUseGeneratedKeys();
        Map<String, ColumnMeta> primaryKeyMap = tableMeta.getPrimaryKeyMap();
        if (primaryKeyMap.size() != 1) {
            useGeneratedKeys = false;
        }

        if (useGeneratedKeys) {
            element.addAttribute("useGeneratedKeys", "true");
            String primartKey = tableMeta.getPrimaryKeyOnlyName().get(0);
            element.addAttribute("keyProperty", primartKey);
        }

        addInsertSql(tableMeta, parentContext.getConfiguration(), element);
        return element;
    }

    protected String getSqlId() {
        return "insert";
    }

    protected void addInsertSql(TableMeta tableMeta, Configuration configuration, Element element) {
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
            paramsBuilder.append("#{").append(columnToCamel(column, configuration)).append("},");
        }
        deleteLastChar(fieldsBuilder);
        deleteLastChar(paramsBuilder);

        String sqlFormat = "insert into %s(%s) %s values (%s)";
        String sql = String.format(sqlFormat, tableName, fieldsBuilder.toString(), tableName, paramsBuilder.toString());
        element.addText(sql);
    }


    @Override
    protected void addMapperMethod(ClassInfo mapperClassInfo, SqlContext sqlContext) {
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setName(getSqlId());
        methodInfo.setDoc(getMethodDoc());
        methodInfo.setReturnType("int");
        methodInfo.getParamInfos().addAll(getParamInfos(mapperClassInfo, sqlContext));
        methodInfo.setIsGeneric(false);
        methodInfo.setAccessor("public");
        methodInfo.setIsAbstract(false);
        methodInfo.setIsInterface(true);
        mapperClassInfo.getMethodInfos().add(methodInfo);
    }

    protected String getMethodDoc() {
        return "新增";
    }

    protected List<ParamInfo> getParamInfos(ClassInfo mapperClassInfo, SqlContext sqlContext) {
        mapperClassInfo.getImports().add(getFullEntityName(sqlContext));
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.setType(getEntityName(sqlContext));
        paramInfo.setName(getEntityFieldName(sqlContext));
        return Arrays.asList(paramInfo);
    }

}
