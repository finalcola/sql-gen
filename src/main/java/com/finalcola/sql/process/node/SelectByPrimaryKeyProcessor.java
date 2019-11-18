package com.finalcola.sql.process.node;

import com.finalcola.sql.process.AbstractNodeProcessor;
import com.finalcola.sql.process.SqlContext;
import com.finalcola.sql.struct.*;
import com.finalcola.sql.util.MysqlKeywordUtils;
import com.finalcola.sql.util.MysqlTypeMap;
import com.finalcola.sql.util.StringUtils;
import lombok.EqualsAndHashCode;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

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
        String tableName = MysqlKeywordUtils.processKeyword(parentContext.getTableMeta().getTableName());
//        String sqlFormat = "select %s from %s %s";
//        String sql = String.format(sqlFormat, getColumns(parentContext, element), tableName, getWhere(parentContext, element));
//        element.addText(sql);
        element.addText("select ");
        addColumns(parentContext, element);
        element.addText(" from " + tableName + " ");
        addWhere(parentContext, element);
        return element;
    }

    protected void addWhere(SqlContext context, Element element) {
        TableMeta tableMeta = context.getTableMeta();
        Element whereNode = createElement("where");
        tableMeta.getPrimaryKeyMap().keySet().stream()
                .map(column -> getIfConditionNode(column, context, "and", ""))
                .forEach(whereNode::add);
        element.add(whereNode);
    }

    protected void addColumns(SqlContext context, Element element) {
        boolean useResultMap = containsNode(context, ResultMapProcessor.TYPE);
        boolean useInclude = containsProccessor(context, BaseColumnSqlProcessor.TYPE);
        Node columnsNode = null;
        TableMeta tableMeta = context.getTableMeta();
        Map<String, ColumnMeta> allColumns = tableMeta.getAllColumns();
        if (useResultMap) {
            element.addAttribute("resultMap", ResultMapProcessor.ID);
            if (useInclude) {
                columnsNode = getColumnsUseInclude(allColumns, context);
            } else {
                columnsNode = getColumnsUseResultMap(allColumns, context);
            }
        } else {
            element.addAttribute("resultType", getFullEntityName(context));
            columnsNode = getColumnsUnuseResultMap(allColumns, context);
        }
        element.add(columnsNode);
    }

    protected Node getColumnsUseInclude(Map<String, ColumnMeta> allColumns, SqlContext context) {
//        return String.format("<include refid=\"%s\"></include>", BaseColumnSqlProcessor.ID);
        Element node = createElement("include");
        node.addAttribute("refid", BaseColumnSqlProcessor.ID);
        return node;
    }

    protected Node getColumnsUseResultMap(Map<String, ColumnMeta> allColumns, SqlContext context) {
        StringBuilder builder = new StringBuilder();
        allColumns.keySet().stream()
                .filter(Objects::nonNull)
                .map(MysqlKeywordUtils::processKeyword)
                .forEach(column -> builder.append(column).append(","));
        builder.deleteCharAt(builder.length() - 1);
        return DocumentHelper.createText(builder.toString());
    }

    protected Node getColumnsUnuseResultMap(Map<String, ColumnMeta> allColumns, SqlContext context) {
        String fields = allColumns.keySet().stream()
                .map(column -> getColumnAsField(column, context))
                .reduce((s1, s2) -> s1 + ", " + s2)
                .orElse("");
        return DocumentHelper.createText(fields);
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
