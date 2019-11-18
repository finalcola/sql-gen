package com.finalcola.sql.process;

import com.finalcola.sql.config.Configuration;
import com.finalcola.sql.struct.ClassInfo;
import com.finalcola.sql.struct.ColumnMeta;
import com.finalcola.sql.util.MysqlKeywordUtils;
import com.finalcola.sql.util.MysqlTypeMap;
import com.finalcola.sql.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.List;
import java.util.Set;

/**
 * @author: yuanyou.
 * @date: 2019-11-12 14:33
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public abstract class AbstractNodeProcessor extends AbstractProcessor {

    @Override
    public void handle(SqlContext sqlContext) {
        if (!support(sqlContext)) {
            return;
        }
        SqlContext subSqlContext = createSubSqlContext(sqlContext);
        if (subSqlContext != null) {
            sqlContext.addSub(subSqlContext);
            ClassInfo mapperClassInfo = sqlContext.getClassInfoMap().get(getFullMapperName(sqlContext));
            if (mapperClassInfo != null) {
                addMapperMethod(mapperClassInfo, sqlContext);
            }
        }
    }

    @Override
    public int getOrder() {
        return 1000 * 100;
    }

    /**
     * 创建子节点
     * @return
     */
    protected abstract SqlContext createSubSqlContext(SqlContext parentContext);

    protected void addMapperMethod(ClassInfo mapperClassInfo, SqlContext sqlContext) {
        // mapper接口添加对应的方法
    }

    /**
     * 是否支持生成该节点
     * @param sqlContext
     * @return
     */
    protected boolean support(SqlContext sqlContext) {
        return true;
    }

    protected Element createElement(String element) {
        return DocumentHelper.createElement(element);
    }

    protected String columnToCamel(String column, Configuration configuration) {
        assert column != null;
        String columnNameTrim = configuration.getColumnNameTrim();
        if (columnNameTrim != null && column.startsWith(columnNameTrim)) {
            column = column.substring(columnNameTrim.length());
        }
        if (!column.contains("_")) {
            return column;
        }
        return StringUtils.toCamel(column, false);
    }

    protected void deleteLastChar(StringBuilder stringBuilder) {
        if (stringBuilder == null || stringBuilder.length() < 1) {
            return;
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
    }

    protected String getIfCondition(String column, SqlContext sqlContext, String prefix, String suffix) {
        ColumnMeta columnMeta = sqlContext.getTableMeta().getAllColumns().get(column);
        String fieldName = columnToCamel(column, sqlContext.getConfiguration());
        String dataTypeName = columnMeta.getDataTypeName();
        String javaType = MysqlTypeMap.getJavaType(dataTypeName);
        String testAdditionalStr = null;
        if ("String".equalsIgnoreCase(javaType)) {
            testAdditionalStr = " and " + fieldName + " != ''";
        } else {
            testAdditionalStr = "";
        }
        String sqlFormat = "<if test=\"%s != null%s\">\n" +
                "                %s %s=#{%s}%s\n" +
                "            </if>";
        return String.format(sqlFormat, fieldName, testAdditionalStr, prefix, MysqlKeywordUtils.processKeyword(column), fieldName, suffix);
    }

    protected Element getIfConditionNode(String column, SqlContext sqlContext, String prefix, String suffix) {
        ColumnMeta columnMeta = sqlContext.getTableMeta().getAllColumns().get(column);
        String fieldName = columnToCamel(column, sqlContext.getConfiguration());
        String dataTypeName = columnMeta.getDataTypeName();
        String javaType = MysqlTypeMap.getJavaType(dataTypeName);
        String testAdditionalStr = null;
        if ("String".equalsIgnoreCase(javaType)) {
            testAdditionalStr = " and " + fieldName + " != ''";
        } else {
            testAdditionalStr = "";
        }
        Element element = createElement("if");
        element.addAttribute("test", String.format("%s != null%s", fieldName, testAdditionalStr));
        element.addText(String.format("%s %s=#{%s}%s", prefix, MysqlKeywordUtils.processKeyword(column), fieldName, suffix));
        return element;
    }

    protected String getSimpleCondition(String column, SqlContext sqlContext, String prefix, String suffix) {
        String sqlFormat = " %s %s=#{%s} %s";
        return String.format(sqlFormat, prefix, MysqlKeywordUtils.processKeyword(column), columnToCamel(column, sqlContext.getConfiguration()), suffix);
    }

    protected boolean containsNode(SqlContext context, String nodeName) {
        if (StringUtils.isBlank(nodeName)) {
            return false;
        }
        List<SqlContext> subList = context.getSub();
        if (subList == null || subList.isEmpty()) {
            return false;
        }
        for (SqlContext sub : subList) {
            if (sub.getNode() != null && sub.getNode().getName().equalsIgnoreCase(nodeName)) {
                return true;
            }
        }
        return false;
    }

    protected boolean containsProccessor(SqlContext context, String type) {
        Set<String> excludeNodes = context.getConfiguration().getExcludeNodes();
        return !excludeNodes.contains(type);
    }

}
