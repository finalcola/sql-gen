package com.finalcola.sql.process.node;

import com.finalcola.sql.anno.ServiceImpl;
import com.finalcola.sql.process.AbstractNodeProcessor;
import com.finalcola.sql.process.SqlContext;
import com.finalcola.sql.struct.ColumnMeta;
import com.finalcola.sql.struct.TableMeta;
import lombok.EqualsAndHashCode;
import org.dom4j.Element;

import java.util.Map;

/**
 * @author: yuanyou.
 * @date: 2019-11-12 14:57
 */
@EqualsAndHashCode(callSuper = false)
@ServiceImpl(name = "resultMap")
public class ResultMapProcessor extends AbstractNodeProcessor {
    /**
     * TYPE
     */
    public static final String TYPE = "resultMap";

    /**
     * ID
     */
    public static final String ID = "BaseResultMap";


    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    protected SqlContext createSubSqlContext(SqlContext parentContext) {
        Element node = createResultMap(parentContext);
        SqlContext sqlContext = new SqlContext();
        sqlContext.setNode(node);
        return sqlContext;
    }

    @Override
    public int getOrder() {
        return 1000;
    }

    private Element createResultMap(SqlContext parentContext) {
        TableMeta tableMeta = parentContext.getTableMeta();
        Element element = createElement("resultMap");
        // resultMap attr
        element.addAttribute("id", ID);
        String fullClassName = getFullEntityName(parentContext);
        element.addAttribute("type", fullClassName);

        // resultMap columns
        Map<String, ColumnMeta> allColumns = tableMeta.getAllColumns();
        Map<String, ColumnMeta> primaryKeyMap = tableMeta.getPrimaryKeyMap();
        for (Map.Entry<String, ColumnMeta> columnMetaEntry : allColumns.entrySet()) {
            String columnName = columnMetaEntry.getKey();
            String fieldName = columnToCamel(columnName, parentContext.getConfiguration());
            boolean isPrimaryKey = primaryKeyMap.containsKey(columnName);
            ColumnMeta columnMeta = columnMetaEntry.getValue();
            String dataTypeName = columnMeta.getDataTypeName();
            String subElementName = (isPrimaryKey) ? "id" : "result";
            Element subElement = element.addElement(subElementName);
            subElement.addAttribute("column", columnName);
            subElement.addAttribute("property", fieldName);
//            subElement.addAttribute("jdbcType", dataTypeName);
        }
        return element;
    }

}
