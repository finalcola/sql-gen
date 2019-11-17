package com.finalcola.sql.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.finalcola.sql.anno.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author: yuanyou.
 * @date: 2019-11-12 09:09
 */
@Slf4j
@Data
@Accessors(chain = true)
public class Configuration {

    @NotNull
    private String driverClass;
    @NotNull
    private String jdbcUrl;
    @NotNull
    private String username;
    @NotNull
    private String password;

    @NotNull
    private String dir;

    @NotNull
    private String packageName;

    private boolean supportLombok = true;
    // TODO: 2019-11-14 暂不支持
    private boolean supportSwagger;

    private boolean useGeneratedKeys = true;

    private DataSource dataSource;

    private Set<String> excludeNodes = new HashSet<>();

    private String columnNameTrim;

    private String tableNameTrim = "t_";

    private String mybatisInterfaceSuffix = "Mapper";

    private String mapperPackageName = "Mapper";

    private String entityPackageName = "bean";

    private String entitySuffix = "Po";

    private String entityParentClass;

    public Configuration addExcludeNode(String nodeType) {
        if (excludeNodes == null) {
            excludeNodes = new HashSet<>();
        }
        excludeNodes.add(nodeType);
        return this;
    }

    public List<String> getTableNames() throws SQLException {
        String catalog = getDatabaseName();
        return execWithConn(con -> {
            ArrayList<String> tableNames = null;
            ResultSet resultSet = null;
            try {
                resultSet = con.getMetaData().getTables(catalog, "%", "%", new String[]{"TABLE"});
                tableNames = new ArrayList<>();
                while (resultSet.next()) {
                    String tableName = resultSet.getString("TABLE_NAME");
                    if ("sys_config".equalsIgnoreCase(tableName)) {
                        continue;
                    }

                    tableNames.add(tableName);
                }
            } finally {
                if (resultSet != null) {
                    resultSet.close();
                }
            }
            return tableNames;
        });
    }

    public DataSource getOrCreateDataSource() {
        if (dataSource == null) {
            synchronized (this) {
                if (dataSource == null) {
                    DruidDataSource druidDataSource = new DruidDataSource();
                    druidDataSource.setDriverClassName(driverClass);
                    druidDataSource.setUrl(jdbcUrl);
                    druidDataSource.setUsername(username);
                    druidDataSource.setPassword(password);
                    druidDataSource.setInitialSize(1);
                    druidDataSource.setMinIdle(1);
                    druidDataSource.setMaxActive(50);
                    druidDataSource.setMinEvictableIdleTimeMillis(300000);
                    druidDataSource.setTimeBetweenEvictionRunsMillis(30000);
                    dataSource = druidDataSource;
                }
            }
        }
        return dataSource;
    }

    private  <T> T execWithConn(ConnectionFunction<T> function) throws SQLException {

        DataSource dataSource = getOrCreateDataSource();
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            return function.apply(connection);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    private String getDatabaseName() {
        if (jdbcUrl == null) {
            return null;
        }
        int startIndex = jdbcUrl.lastIndexOf("/");
        int endIndex = jdbcUrl.lastIndexOf("?");
        if (startIndex != -1 && endIndex != -1) {
            return jdbcUrl.substring(startIndex + 1, endIndex);
        }
        return null;
    }

}
