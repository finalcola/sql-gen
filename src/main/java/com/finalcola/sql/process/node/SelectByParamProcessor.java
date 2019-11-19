package com.finalcola.sql.process.node;

import com.finalcola.sql.anno.ServiceImpl;
import com.finalcola.sql.process.SqlContext;
import com.finalcola.sql.struct.*;
import com.finalcola.sql.util.StringUtils;
import lombok.EqualsAndHashCode;
import org.dom4j.Element;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author: yuanyou.
 * @date: 2019-11-13 17:23
 */
@EqualsAndHashCode(callSuper = true)
@ServiceImpl(name = "selectByParam")
public class SelectByParamProcessor extends SelectByPrimaryKeyProcessor {

    @Override
    public String getType() {
        return "selectByParam";
    }

    @Override
    public int getOrder() {
        return super.getOrder() + 6;
    }

    @Override
    protected boolean support(SqlContext sqlContext) {
        Map<String, ColumnMeta> columnsExcludePk = sqlContext.getTableMeta().getColumnsExcludePk();
        return columnsExcludePk.size() > 0;
    }

    @Override
    protected void addWhere(SqlContext context, Element element) {
        TableMeta tableMeta = context.getTableMeta();
        Map<String, ColumnMeta> columnnMap = tableMeta.getColumnsExcludePk();
        Element whereNode = createElement("where");
        columnnMap.keySet().stream()
                .map(column -> getIfConditionNode(column, context, "and", ""))
                .forEach(whereNode::add);
        element.add(whereNode);
    }

    @Override
    protected String getMethodDoc() {
        return "根据参数查询";
    }

    @Override
    protected List<ParamInfo> getParamInfos(ClassInfo mapperClassInfo, SqlContext sqlContext) {
        mapperClassInfo.getImports().add(getFullEntityName(sqlContext));
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.setType(getEntityName(sqlContext));
        paramInfo.setName(getEntityFieldName(sqlContext));
        return Arrays.asList(paramInfo);
    }

}
