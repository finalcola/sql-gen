package com.finalcola.sql.process.consumer;

import com.finalcola.sql.config.Configuration;
import com.finalcola.sql.process.SqlContext;
import com.finalcola.sql.util.MysqlKeywordUtils;
import com.finalcola.sql.util.StringUtils;

import java.io.File;

/**
 * @author: yuanyou.
 * @date: 2019-11-14 09:41
 */
public abstract class AbstractSqlContextConsumer implements SqlContextConsumer {

    protected String getMapperName(SqlContext sqlContext) {
        Configuration configuration = sqlContext.getConfiguration();
        String tableName = getTableName(sqlContext, false);
        String mapperName = StringUtils.toCamel(tableName, true);
        if (StringUtils.isNotBlank(configuration.getMybatisInterfaceSuffix())) {
            mapperName = mapperName + configuration.getMybatisInterfaceSuffix();
        }
        return mapperName;
    }

    protected String getEntityName(SqlContext sqlContext) {
        String tableName = getTableName(sqlContext, false);
        String entityName = StringUtils.toCamel(tableName, true);
        Configuration configuration = sqlContext.getConfiguration();
        if (configuration.getEntitySuffix() != null) {
            entityName = entityName + configuration.getEntitySuffix();
        }
        return entityName;
    }

    protected String getTableName(SqlContext sqlContext, boolean filterKeyword) {
        String tableName = sqlContext.getTableMeta().getTableName();
        assert tableName != null;
        Configuration configuration = sqlContext.getConfiguration();
        String tableNameTrim = configuration.getTableNameTrim();
        if (tableName.startsWith(tableNameTrim)) {
            tableName = tableName.substring(tableNameTrim.length());
        }
        if (filterKeyword) {
            tableName = MysqlKeywordUtils.processKeyword(tableName);
        }
        return tableName;
    }

}
