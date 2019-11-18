package com.finalcola.sql.process.node;

import com.finalcola.sql.process.AbstractNodeProcessor;
import com.finalcola.sql.process.SqlContext;
import com.finalcola.sql.struct.*;
import com.finalcola.sql.util.MysqlKeywordUtils;
import com.finalcola.sql.util.MysqlTypeMap;
import com.finalcola.sql.util.StringUtils;
import lombok.EqualsAndHashCode;
import org.dom4j.Element;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: yuanyou.
 * @date: 2019-11-13 09:02
 */
@EqualsAndHashCode(callSuper = true)
public class SelectByPrimaryKeyProcessor extends AbstractNodeProcessor {
    @Override
    public String getType() {
        return "selectByPrimaryKey";
    }

    @Override
    public int getOrder() {
        return super.getOrder() + 5;
    }

    @Override
    protected SqlContext createSubSqlContext(SqlContext parentContext) {
        if (parentContext.getTableMeta().getPrimaryKeyMap().isEmpty()) {
            return null;
        }
        Element node = createSelectByPrimaryKey(parentContext);
        if (node == null) {
            return null;
        }
        SqlContext sqlContext = new SqlContext();
        sqlContext.setNode(node);
        return sqlContext;
    }

    @Override
    protected boolean support(SqlContext sqlContext) {
        // 主键数量大于0，才支持生成该SQL
        TableMeta tableMeta = sqlContext.getTableMeta();
        return tableMeta.getPrimaryKeyOnlyName().size() > 0;
    }

    protected Element createSelectByPrimaryKey(SqlContext parentContext) {
        Element element = createElement("select");
        element.addAttribute("id", getType());
        String sqlFormat = "select %s from %s %s";
        String tableName = MysqlKeywordUtils.processKeyword(parentContext.getTableMeta().getTableName());
        String sql = String.format(sqlFormat, getColumns(parentContext, element), tableName, getWhere(parentContext, element));
        element.addText(sql);
        return element;
    }

    protected String getWhere(SqlContext context, Element element) {
        TableMeta tableMeta = context.getTableMeta();
        StringBuilder builder = new StringBuilder("<where>");
        tableMeta.getPrimaryKeyMap().keySet().stream()
                .map(column -> getIfCondition(column, context, "and", ""))
                .forEach(builder::append);
        builder.append("</where>");
        return builder.toString();
    }

    protected String getColumns(SqlContext context, Element element) {
        boolean useResultMap = containsNode(context, ResultMapProcessor.TYPE);
        boolean useInclude = containsProccessor(context, BaseColumnSqlProcessor.TYPE);
        String columns = null;
        TableMeta tableMeta = context.getTableMeta();
        Map<String, ColumnMeta> allColumns = tableMeta.getAllColumns();
        if (useResultMap) {
            element.addAttribute("resultMap", ResultMapProcessor.ID);
            if (useInclude) {
                columns = getColumnsUseInclude(allColumns, context);
            } else {
                columns = getColumnsUseResultMap(allColumns, context);
            }
        } else {
            element.addAttribute("resultType", getFullEntityName(context));
            columns = getColumnsUnuseResultMap(allColumns, context);
        }
        return columns;
    }

    protected String getColumnsUseInclude(Map<String, ColumnMeta> allColumns, SqlContext context) {
        return String.format("<include refid=\"%s\"></include>", BaseColumnSqlProcessor.ID);
    }

    protected String getColumnsUseResultMap(Map<String, ColumnMeta> allColumns, SqlContext context) {
        StringBuilder builder = new StringBuilder();
        allColumns.keySet().stream()
                .map(MysqlKeywordUtils::processKeyword)
                .filter(Objects::nonNull)
                .forEach(column -> builder.append(column).append(","));
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    protected String getColumnsUnuseResultMap(Map<String, ColumnMeta> allColumns, SqlContext context) {
        String fields = allColumns.keySet().stream()
                .map(MysqlKeywordUtils::processKeyword)
                .map(column -> getColumnAsField(column, context))
                .reduce((s1, s2) -> s1 + ", " + s2)
                .orElse("");
        return fields;
    }

    protected String getColumnAsField(String column, SqlContext context) {
        String field = columnToCamel(column, context.getConfiguration());
        StringBuilder builder = new StringBuilder();
        builder.append(" ").append(MysqlKeywordUtils.processKeyword(column)).append(" as ").append(field);
        return builder.toString();
    }

    @Override
    protected void addMapperMethod(ClassInfo mapperClassInfo, SqlContext sqlContext) {
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setName(getType());
        methodInfo.setDoc(getMethodDoc());
        mapperClassInfo.getImports().add(getFullEntityName(sqlContext));
        methodInfo.setReturnType(getEntityName(sqlContext));
        methodInfo.getParamInfos().addAll(getParamInfos(mapperClassInfo, sqlContext));
        methodInfo.setIsGeneric(false);
        methodInfo.setAccessor("public");
        methodInfo.setIsAbstract(false);
        methodInfo.setIsInterface(true);
        mapperClassInfo.getMethodInfos().add(methodInfo);
    }

    protected String getMethodDoc() {
        return "根据参数查询";
    }

    protected List<ParamInfo> getParamInfos(ClassInfo mapperClassInfo, SqlContext sqlContext) {
        Map<String, ColumnMeta> primaryKeyMap = sqlContext.getTableMeta().getPrimaryKeyMap();
        if (primaryKeyMap.isEmpty()) {
            return Collections.emptyList();
        }
        mapperClassInfo.getImports().add("org.apache.ibatis.annotations.Param");
        List<ParamInfo> paramInfoList = primaryKeyMap.entrySet().stream()
                .map((entry) -> {
                    String column = entry.getKey();
                    String fieldName = columnToCamel(column, sqlContext.getConfiguration());
                    ColumnMeta columnMeta = entry.getValue();
                    ParamInfo paramInfo = new ParamInfo();
                    if (MysqlTypeMap.isNotBasicType(columnMeta.getDataTypeName())) {
                        mapperClassInfo.getImports().add(MysqlTypeMap.getFullJavaType(columnMeta.getDataTypeName()));
                    }
                    paramInfo.setType(MysqlTypeMap.getJavaType(columnMeta.getDataTypeName()));
                    paramInfo.setName(fieldName);
                    paramInfo.getAnnotaions().add("@Param(\"" + fieldName + "\")");
                    return paramInfo;
                })
                .collect(Collectors.toList());
        return paramInfoList;
    }

}
