package com.finalcola.sql.struct;

import com.alibaba.druid.pool.DruidDataSource;
import com.finalcola.sql.config.Configuration;
import com.finalcola.sql.process.NodeProcessorChain;
import com.finalcola.sql.process.ContextManager;
import com.finalcola.sql.process.XmlProcessor;
import com.finalcola.sql.process.node.*;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.junit.Test;

import javax.sql.DataSource;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.HashSet;

/**
 * @author: yuanyou.
 * @date: 2019-11-11 20:38
 */
public class TableMetaCacheTest {

    @Test
    public void genSql() throws SQLException {
        Configuration configuration = new Configuration();
        configuration.setDriverClass("com.mysql.cj.jdbc.Driver")
                .setJdbcUrl("jdbc:mysql://localhost:3306/local?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC")
                .setUsername("finalcola")
                .setPassword("z845447141")
                .setDir("tmp/sql-gen")
                .setPackageName("com.finalcola");
        ContextManager contextManager = new ContextManager(configuration);
        contextManager.start();
    }

    private void printDom(Document document) {
        OutputFormat outputFormat = OutputFormat.createPrettyPrint();
        outputFormat.setEncoding("UTF-8");
        outputFormat.setIndent(true);
        outputFormat.setExpandEmptyElements(true);
        outputFormat.setNewlines(true);
        StringWriter stringWriter = new StringWriter();
        XMLWriter xmlWriter = new XMLWriter(stringWriter, outputFormat);
        xmlWriter.setEscapeText(false);
        try {
            xmlWriter.write(document);
            xmlWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                xmlWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(stringWriter.toString());
    }

    private NodeProcessorChain createNodeProcessorChain() {
        NodeProcessorChain chain = new NodeProcessorChain();
        chain.addProcessor(new ResultMapProcessor());
        chain.addProcessor(new BaseColumnSqlProcessor());
        chain.addProcessor(new InsertProcessor());
        chain.addProcessor(new InsertBatchProcessor());
        chain.addProcessor(new DeleteProcessor());
        chain.addProcessor(new SelectByPrimaryKeyProcessor());
        chain.addProcessor(new SelectByParamProcessor());
        chain.addProcessor(new UpdateProcessor());
        return chain;
    }

    private XmlProcessor createXmlProcessor(NodeProcessorChain chain) {
        return new XmlProcessor(chain);
    }

    @Test
    public void getTableMeta() {
        TableMeta tableMeta = TableMetaCache.getTableMeta("t_meeting", createDateSource());
        HashSet<String> dbTypeNameSet = new HashSet<>();
        tableMeta.getAllColumns().values().forEach(columnMeta -> dbTypeNameSet.add(columnMeta.getDataTypeName()));
        System.out.println(dbTypeNameSet);
    }

    private DataSource createDateSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://172.18.8.46:3306/tms?useUnicode=true&characterEncoding=utf-8");
        dataSource.setUsername("tmsuser");
        dataSource.setPassword("tmspwd");
        dataSource.setMinIdle(1);
        dataSource.setMaxActive(20);
        dataSource.setInitialSize(2);
        dataSource.setValidationQuery("select 1");
        dataSource.setTestWhileIdle(true);
        dataSource.setTimeBetweenEvictionRunsMillis(29);
        return dataSource;
    }
}