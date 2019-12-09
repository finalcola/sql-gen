package com.finalcola.sql.struct;

import com.alibaba.druid.pool.DruidDataSource;
import com.finalcola.sql.SqlGen;
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
public class SqlGenTest {

    @Test
    public void genSql() throws SQLException {
        Configuration configuration = new Configuration();
        configuration.setDriverClass("com.mysql.cj.jdbc.Driver")
                .setJdbcUrl("jdbc:mysql://localhost:3306/fbasic?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC")
                .setUsername("dbuser")
                .setPassword("123456")
                .setDir("tmp/gen")
                .setPackageName("com.finalcola");
        SqlGen.generateSql(configuration);
    }

}