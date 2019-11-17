package com.finalcola.sql.process;

import com.finalcola.sql.config.Configuration;
import com.finalcola.sql.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: yuanyou.
 * @date: 2019-11-12 14:21
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class XmlProcessor extends AbstractProcessor {

    NodeProcessorChain nodeProcessorChain;

    @Override
    public String getType() {
        return "xml";
    }

    @Override
    public void handle(SqlContext sqlContext) {
        sqlContext.setNode(createRootElement(sqlContext));
        nodeProcessorChain.handle(sqlContext);
        sqlContext.addMergeFun((context) -> {
            Element root = context.getNode();
            List<SqlContext> subContexts = context.getSub();
            ArrayList<Element> subElements = new ArrayList<>(subContexts.size());
            for (SqlContext subContext : subContexts) {
                subContext.merge();
                if (subContext.getNode() != null) {
                    subElements.add(subContext.getNode());
                }
            }
            for (Element element : subElements) {
                root.add(element);
            }
            // clear sub ref
            context.setSub(null);
        });
    }

    private Element createRootElement(SqlContext sqlContext) {
        Document document = DocumentHelper.createDocument();
        document.addDocType("mapper", "-//mybatis.org//DTD Mapper 3.0//EN", "http://mybatis.org/dtd/mybatis-3-mapper.dtd");
        Element root = document.addElement("mapper");
        root.addAttribute("namespace", getFullMapperClass(sqlContext));
        return root;
    }

    private String getMapperClass(SqlContext sqlContext) {
        String tableName = sqlContext.getTableMeta().getTableName();
        assert tableName != null;
        Configuration configuration = sqlContext.getConfiguration();
        String tableNameTrim = configuration.getTableNameTrim();
        if (tableNameTrim != null && tableName.startsWith(tableNameTrim)) {
            tableName = tableName.substring(tableNameTrim.length());
        }
        return StringUtils.toCamel(tableName, true) + configuration.getMybatisInterfaceSuffix();
    }

    private String getFullMapperClass(SqlContext sqlContext) {
        String packageName = sqlContext.getConfiguration().getPackageName();
        return packageName + "." + getMapperClass(sqlContext);
    }
}
