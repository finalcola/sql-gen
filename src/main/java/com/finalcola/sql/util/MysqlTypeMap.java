package com.finalcola.sql.util;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author: yuanyou.
 * @date: 2019-11-12 20:26
 */
@Slf4j
public final class MysqlTypeMap {

    private MysqlTypeMap() {

    }

    private static final Map<String, String> MYSQL_TO_JAVA = new HashMap<>();

    private static final Map<String, String> SPECIAL = new HashMap<>();

    static {
        MYSQL_TO_JAVA.put("INT", "Integer");
        MYSQL_TO_JAVA.put("SMALLINT", "Integer");
        MYSQL_TO_JAVA.put("MEDIUMINT", "Integer");
        MYSQL_TO_JAVA.put("INTEGER", "Integer");
        MYSQL_TO_JAVA.put("BIGINT", "BigInteger");
        MYSQL_TO_JAVA.put("FLOAT", "Float");
        MYSQL_TO_JAVA.put("DECIMAL", "BigDecimal");
        MYSQL_TO_JAVA.put("INT UNSIGNED", "Integer");
        MYSQL_TO_JAVA.put("TINYINT", "Integer");
        MYSQL_TO_JAVA.put("TINYINT UNSIGNED", "Integer");
        MYSQL_TO_JAVA.put("SMALL UNSIGNED", "Integer");
        MYSQL_TO_JAVA.put("MEDIUMINT UNSIGNED", "Integer");
        MYSQL_TO_JAVA.put("BIT", "Integer");    // maybe waste
        MYSQL_TO_JAVA.put("BIGINT UNSIGNED", "BigInteger");
        MYSQL_TO_JAVA.put("DOUBLE", "Double");
        MYSQL_TO_JAVA.put("PK", "Long");
        MYSQL_TO_JAVA.put("PK(TINYINT UNSIGNED)", "Long");


        MYSQL_TO_JAVA.put("TEXT", "String");
        MYSQL_TO_JAVA.put("VARCHAR", "String");
        MYSQL_TO_JAVA.put("TINYTEXT", "String");
        MYSQL_TO_JAVA.put("MEDIUMTEXT", "String");
        MYSQL_TO_JAVA.put("LONGTEXT", "String");


        MYSQL_TO_JAVA.put("DATE", "Date");
        MYSQL_TO_JAVA.put("TIME", "Date");
        MYSQL_TO_JAVA.put("YEAR", "Date");
        MYSQL_TO_JAVA.put("DATETIME", "Date");
        MYSQL_TO_JAVA.put("TIMESTAMP", "Date");

        MYSQL_TO_JAVA.put("LONGBLOB", "byte[]");
        MYSQL_TO_JAVA.put("TINYBLOB", "byte[]");
        MYSQL_TO_JAVA.put("BLOB", "byte[]");
        MYSQL_TO_JAVA.put("MEDIUMBLOB", "byte[]");

        SPECIAL.put("DECIMAL", "java.math.BigDecimal");
        SPECIAL.put("BIGINT", "java.math.BigInteger");
        SPECIAL.put("BIGINT UNSIGNED", "java.math.BigInteger");
        SPECIAL.put("DATE", "java.util.Date");
        SPECIAL.put("TIME", "java.util.Date");
        SPECIAL.put("YEAR", "java.util.Date");
        SPECIAL.put("DATETIME", "java.util.Date");
        SPECIAL.put("TIMESTAMP", "java.util.Date");
    }

    public static String getJavaType(String dbType) {
        String type = MYSQL_TO_JAVA.get(dbType);
        if (type == null) {
            log.info("未知数据库类型:{}", dbType);
        }
        return type;
    }

    public static String getFullJavaType(String dbType) {
        String type = SPECIAL.getOrDefault(dbType, MYSQL_TO_JAVA.get(dbType));
        if (type == null) {
            log.info("未知数据库类型:{}", dbType);
        }
        return type;
    }

    public static boolean isNotBasicType(String dbType) {
        return SPECIAL.get(dbType) != null;
    }

    public static boolean isBasicType(String dbType) {
        return !isNotBasicType(dbType);
    }

}
