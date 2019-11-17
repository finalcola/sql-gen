package com.finalcola.sql.process;

import com.alibaba.druid.sql.visitor.functions.Char;
import com.finalcola.sql.config.Configuration;
import com.finalcola.sql.util.StringUtils;

/**
 * @author: yuanyou.
 * @date: 2019-11-13 18:47
 */
public abstract class AbstractProcessor implements Processor,Order {

    public abstract String getType();

    protected String getTableName(SqlContext sqlContext) {
        return sqlContext.getTableMeta().getTableName();
    }

    protected String getClassName(SqlContext sqlContext) {
        String tableName = getTableName(sqlContext);
        assert tableName != null;
        Configuration configuration = sqlContext.getConfiguration();
        String tableNameTrim = configuration.getTableNameTrim();
        if (tableName.startsWith(tableNameTrim)) {
            tableName = tableName.substring(tableNameTrim.length());
        }
        String entityClassName = StringUtils.toCamel(tableName, true);
//        if (StringUtils.isNotBlank(configuration.getEntitySuffix())) {
//            entityClassName = entityClassName + configuration.getEntitySuffix();
//        }
        return entityClassName;
    }

    protected String getFullClassName(SqlContext sqlContext) {
        String packageName = sqlContext.getConfiguration().getPackageName();
        return packageName + "." + getClassName(sqlContext);
    }

    protected String getMapperName(SqlContext sqlContext) {
        String suffix = sqlContext.getConfiguration().getMybatisInterfaceSuffix();
        if (StringUtils.isBlank(suffix)) {
            suffix = "Mapper";
        }
        return getClassName(sqlContext) + suffix;
    }

    protected String getFullMapperName(SqlContext sqlContext) {
        String suffix = sqlContext.getConfiguration().getMybatisInterfaceSuffix();
        if (StringUtils.isBlank(suffix)) {
            suffix = "Mapper";
        }
        return getFullClassName(sqlContext) + suffix;
    }

    protected String getEntityName(SqlContext sqlContext) {
        String entitySuffix = sqlContext.getConfiguration().getEntitySuffix();
        if (StringUtils.isBlank(entitySuffix)) {
            entitySuffix = "Po";
        }
        return getClassName(sqlContext) + entitySuffix;
    }

    protected String getEntityFieldName(SqlContext sqlContext) {
        String entityName = getEntityName(sqlContext);
        char[] charArray = entityName.toCharArray();
        charArray[0] = Character.toLowerCase(charArray[0]);
        return String.valueOf(charArray);
    }

    protected String getFullEntityName(SqlContext sqlContext) {
        String entitySuffix = sqlContext.getConfiguration().getEntitySuffix();
        if (StringUtils.isBlank(entitySuffix)) {
            entitySuffix = "Po";
        }
        return getFullClassName(sqlContext) + entitySuffix;
    }
}
