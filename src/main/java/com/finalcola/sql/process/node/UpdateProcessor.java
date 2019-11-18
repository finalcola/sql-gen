package com.finalcola.sql.process.node;

import com.finalcola.sql.process.AbstractNodeProcessor;
import com.finalcola.sql.process.SqlContext;
import com.finalcola.sql.struct.*;
import com.finalcola.sql.util.MysqlKeywordUtils;
import com.finalcola.sql.util.MysqlTypeMap;
import com.finalcola.sql.util.StringUtils;
import lombok.EqualsAndHashCode;
import org.dom4j.Element;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: yuanyou.
 * @date: 2019-11-13 17:35
 */
@EqualsAndHashCode(callSuper = true)
public class UpdateProcessor extends AbstractNodeProcessor {

    @Override
    public String getType() {
        return "updateByPrimaryKey";
    }

    @Override
    public int getOrder() {
        return super.getOrder() + 4;
    }

    @Override
    protected SqlContext createSubSqlContext(SqlContext parentContext) {
        if (parentContext.getTableMeta().getPrimaryKeyMap().isEmpty()) {
            return null;
        }
        Element node = createUpdate(parentContext);
        if (node == null) {
            return null;
        }
        SqlContext sqlContext = new SqlContext();
        sqlContext.setNode(node);
        return sqlContext;
    }

    @Override
    protected boolean support(SqlContext sqlContext) {
        TableMeta tableMeta = sqlContext.getTableMeta();
        return !tableMeta.getPrimaryKeyOnlyName().isEmpty() && !tableMeta.getColumnsExcludePk().isEmpty();
    }

    private Element createUpdate(SqlContext parentContext) {
        Element element = createElement("update");
        element.addAttribute("id", getType());
        element.addAttribute("parameterType", getFullEntityName(parentContext));
        String format = "\nupdate %s %s where %s";
        String sql = String.format(format, MysqlKeywordUtils.processKeyword(getTableName(parentContext)), getSet(parentContext), getWhere(parentContext));
        element.addText(sql);
        return element;
    }

    protected String getSet(SqlContext sqlContext) {
        TableMeta tableMeta = sqlContext.getTableMeta();
        Map<String, ColumnMeta> columnMap = tableMeta.getColumnsExcludePk();
        StringBuilder builder = new StringBuilder("<set>");
        columnMap.keySet().stream()
                .map(column -> getIfCondition(column, sqlContext, ",", ""))
                .forEach(builder::append);
        builder.append("</set>");
        return builder.toString();
    }

    protected String getWhere(SqlContext sqlContext) {
        StringBuilder builder = new StringBuilder();
        List<String> primaryKeyList = sqlContext.getTableMeta().getPrimaryKeyOnlyName();
        primaryKeyList.stream()
                .map(MysqlKeywordUtils::processKeyword)
                .map(column -> getSimpleCondition(column, sqlContext, "", ""))
                .forEach(builder::append);
        return builder.toString();
    }

    @Override
    protected void addMapperMethod(ClassInfo mapperClassInfo, SqlContext sqlContext) {
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setName(getType());
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
        return "更新";
    }

    protected List<ParamInfo> getParamInfos(ClassInfo mapperClassInfo, SqlContext sqlContext) {
        mapperClassInfo.getImports().add(getFullEntityName(sqlContext));
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.setType(getEntityName(sqlContext));
        paramInfo.setName(getEntityFieldName(sqlContext));
        return Arrays.asList(paramInfo);
    }

}
