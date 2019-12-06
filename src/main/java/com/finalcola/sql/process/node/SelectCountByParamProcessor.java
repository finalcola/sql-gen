package com.finalcola.sql.process.node;

import com.finalcola.sql.anno.ServiceImpl;
import com.finalcola.sql.process.SqlContext;
import com.finalcola.sql.struct.ColumnMeta;
import com.finalcola.sql.struct.TableMeta;
import lombok.EqualsAndHashCode;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.Map;

/**
 * @author: yuanyou.
 * @date: 2019-12-06 18:35
 */
@EqualsAndHashCode(callSuper = true)
@ServiceImpl(name = "selectCountByParam")
public class SelectCountByParamProcessor extends SelectByParamProcessor {

    @Override
    public String getType() {
        return "selectCountByParam";
    }

    @Override
    public int getOrder() {
        return super.getOrder() + 5;
    }

    @Override
    protected boolean support(SqlContext sqlContext) {
        Map<String, ColumnMeta> columnsExcludePk = sqlContext.getTableMeta().getColumnsExcludePk();
        return columnsExcludePk.size() > 0;
    }

    @Override
    protected void addColumns(SqlContext context, Element element) {
        boolean useResultMap = containsNode(context, ResultMapProcessor.TYPE);
        if (useResultMap) {
            element.addAttribute("resultMap", ResultMapProcessor.ID);
        } else {
            element.addAttribute("resultType", getFullEntityName(context));
        }
        element.addText("count(*)");
    }

    @Override
    protected String getMethodDoc() {
        return "根据参数查询总数";
    }
}
