package com.finalcola.sql.process.node;

import com.finalcola.sql.anno.ServiceImpl;
import com.finalcola.sql.process.AbstractNodeProcessor;
import com.finalcola.sql.process.SqlContext;
import com.finalcola.sql.struct.*;
import com.finalcola.sql.util.MysqlKeywordUtils;
import com.finalcola.sql.util.MysqlTypeMap;
import com.finalcola.sql.util.StringUtils;
import lombok.EqualsAndHashCode;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: yuanyou.
 * @date: 2019-11-12 19:51
 */
@EqualsAndHashCode(callSuper = false)
@ServiceImpl(name = "delete")
public class DeleteProcessor extends AbstractNodeProcessor {
    @Override
    public String getType() {
        return "delete";
    }

    @Override
    public int getOrder() {
        return super.getOrder() + 3;
    }

    @Override
    protected SqlContext createSubSqlContext(SqlContext parentContext) {
        if (parentContext.getTableMeta().getPrimaryKeyMap().isEmpty()) {
            return null;
        }
        Element node = createDelete(parentContext);
        if (node == null) {
            return null;
        }
        SqlContext sqlContext = new SqlContext();
        sqlContext.setNode(node);
        return sqlContext;
    }

    @Override
    protected boolean support(SqlContext sqlContext) {
        return super.support(sqlContext) && sqlContext.getTableMeta().getPrimaryKeyMap().size() > 0;
    }

    @Override
    protected void addMapperMethod(ClassInfo mapperClassInfo, SqlContext sqlContext) {
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setName(getId());
        methodInfo.setDoc("根据主键删除");
        methodInfo.setReturnType("int");
        methodInfo.getParamInfos().addAll(getParamInfos(sqlContext, mapperClassInfo));
        methodInfo.setIsGeneric(false);
        methodInfo.setAccessor("public");
        methodInfo.setIsAbstract(false);
        methodInfo.setIsInterface(true);
        mapperClassInfo.getMethodInfos().add(methodInfo);
    }

    protected List<ParamInfo> getParamInfos(SqlContext sqlContext, ClassInfo classInfo) {
        Map<String, ColumnMeta> primaryKeyMap = sqlContext.getTableMeta().getPrimaryKeyMap();
        if (primaryKeyMap.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<ParamInfo> paramInfos = new ArrayList<>(primaryKeyMap.size());
        for (Map.Entry<String, ColumnMeta> entry : primaryKeyMap.entrySet()) {
            ParamInfo paramInfo = new ParamInfo();
            String dataTypeName = entry.getValue().getDataTypeName();
            if (MysqlTypeMap.isNotBasicType(dataTypeName)) {
                classInfo.getImports().add(MysqlTypeMap.getFullJavaType(dataTypeName));
            }
            paramInfo.setType(MysqlTypeMap.getJavaType(dataTypeName));
            String fieldName = StringUtils.toCamel(entry.getKey(), false);
            paramInfo.setName(fieldName);
            paramInfo.getAnnotaions().add("@Param(\"" + fieldName + "\")");
            classInfo.getImports().add("org.apache.ibatis.annotations.Param");
            paramInfos.add(paramInfo);
        }
        return paramInfos;
    }

    protected String getId() {
        return "deleteByPrimaryKey";
    }

    protected Element createDelete(SqlContext parentContext) {
        Element element = createElement("delete");
        element.addAttribute("id", getId());
        TableMeta tableMeta = parentContext.getTableMeta();
        List<String> primaryKeyList = tableMeta.getPrimaryKeyOnlyName();
        Element subNode = createElement("where");
        primaryKeyList.stream()
                .map(key -> getIfConditionNode(key, parentContext, "and ", ""))
                .forEach(subNode::add);

        String whereSql = "delete from " + MysqlKeywordUtils.processKeyword(getTableName(parentContext));
        element.addText(whereSql);
        element.add(subNode);
        return element;
    }

}
