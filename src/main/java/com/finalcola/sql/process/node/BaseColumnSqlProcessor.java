package com.finalcola.sql.process.node;

import com.finalcola.sql.anno.ServiceImpl;
import com.finalcola.sql.process.AbstractNodeProcessor;
import com.finalcola.sql.process.SqlContext;
import com.finalcola.sql.struct.ColumnMeta;
import com.finalcola.sql.struct.TableMeta;
import com.finalcola.sql.util.MysqlKeywordUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dom4j.Element;

import java.util.Map;

/**
 * @author: yuanyou.
 * @date: 2019-11-12 17:41
 */
@EqualsAndHashCode(callSuper = false)
@ServiceImpl(name = "baseColumn")
public class BaseColumnSqlProcessor extends AbstractNodeProcessor {
    /**
     * TYPE
     */
    public static final String TYPE = "baseColumn";

    public static final String ID = "BaseColumn";


    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    protected SqlContext createSubSqlContext(SqlContext parentContext) {
        Element node = createBaseColumn(parentContext);
        SqlContext sqlContext = new SqlContext();
        sqlContext.setNode(node);
        return sqlContext;
    }

    @Override
    public int getOrder() {
        return 1001;
    }

    private Element createBaseColumn(SqlContext parentContext) {
        TableMeta tableMeta = parentContext.getTableMeta();
        Element element = createElement("sql");
        element.addAttribute("id", ID);

        // fields
        Map<String, ColumnMeta> allColumns = tableMeta.getAllColumns();
        String content = allColumns.keySet().stream()
                .map(MysqlKeywordUtils::processKeyword)
                .reduce((s1, s2) -> s1 + "," + s2)
                .orElse("");
        element.addText(content);
        return element;
    }
}
