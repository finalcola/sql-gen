package com.finalcola.sql.struct;

import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * @author: yuanyou.
 * @date: 2019-11-11 20:13
 */
@Slf4j
public class TableMetaCache {

    private static final ConcurrentMap<String/*tableName*/, TableMeta> CACHE = new ConcurrentHashMap<>();

    public static TableMeta getTableMeta(String tableName, DataSource dataSource) {
        TableMeta tableMeta = CACHE.get(tableName);
        if (tableMeta == null) {
            synchronized (CACHE) {
                tableMeta = CACHE.get(tableName);
                if (tableMeta != null) {
                    return tableMeta;
                }
                // create new
                try {
                    return fetchSchema(tableName, dataSource);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return tableMeta;
    }

    private static TableMeta fetchSchema(String tableName, DataSource dataSource) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select * from `" + tableName + "` limit 1");
            ResultSetMetaData rsmd = resultSet.getMetaData();
            DatabaseMetaData dbmd = connection.getMetaData();
            return fetchSchema(rsmd, dbmd, tableName);
        } catch (Exception e) {
            if (e instanceof SQLException) {
                throw e;
            }
            throw new SQLException("Failed to fetch schema of " + tableName, e);
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    private static TableMeta fetchSchema(ResultSetMetaData rsmd, DatabaseMetaData dbmd, String tableName) throws SQLException {
        String schemaName = rsmd.getSchemaName(1);
        String catalogName = rsmd.getCatalogName(1);

        TableMeta tm = new TableMeta();
        tm.setTableName(tableName);

        ResultSet rs1 = dbmd.getColumns(catalogName, schemaName, tableName, "%");
        Map<String, ColumnMeta> allColumns = new HashMap<>(32);
        while (rs1.next()) {
            // 解析每个字段的元数据
            ColumnMeta col = new ColumnMeta();
            col.setTableCat(rs1.getString("TABLE_CAT"));
            col.setTableSchemaName(rs1.getString("TABLE_SCHEM"));
            col.setTableName(rs1.getString("TABLE_NAME"));
            col.setColumnName(rs1.getString("COLUMN_NAME"));
            col.setDataType(rs1.getInt("DATA_TYPE"));
            col.setDataTypeName(rs1.getString("TYPE_NAME"));
            col.setColumnSize(rs1.getInt("COLUMN_SIZE"));
            col.setDecimalDigits(rs1.getInt("DECIMAL_DIGITS"));
            col.setNumPrecRadix(rs1.getInt("NUM_PREC_RADIX"));
            col.setNullAble(rs1.getInt("NULLABLE"));
            col.setRemarks(rs1.getString("REMARKS"));
            col.setColumnDef(rs1.getString("COLUMN_DEF"));
            col.setSqlDataType(rs1.getInt("SQL_DATA_TYPE"));
            col.setSqlDatetimeSub(rs1.getInt("SQL_DATETIME_SUB"));
            col.setCharOctetLength(rs1.getInt("CHAR_OCTET_LENGTH"));
            col.setOrdinalPosition(rs1.getInt("ORDINAL_POSITION"));
            col.setIsNullAble(rs1.getString("IS_NULLABLE"));
            col.setIsAutoincrement(rs1.getString("IS_AUTOINCREMENT"));

            allColumns.put(col.getColumnName(), col);
        }

        // 解析索引信息
        ResultSet rs2 = dbmd.getIndexInfo(catalogName, schemaName, tableName, false, true);
        String indexName = "";
        while (rs2.next()) {
            indexName = rs2.getString("INDEX_NAME");
            String colName = rs2.getString("COLUMN_NAME");
            // 上面解析的字段
            ColumnMeta col = allColumns.get(colName);

            if (tm.getAllIndexes().containsKey(indexName)) {
                // 已有该索引，添加该字段
                IndexMeta index = tm.getAllIndexes().get(indexName);
                index.getValues().add(col);
            } else {
                IndexMeta index = new IndexMeta();
                index.setIndexName(indexName);
                index.setNonUnique(rs2.getBoolean("NON_UNIQUE"));
                index.setIndexQualifier(rs2.getString("INDEX_QUALIFIER"));
                index.setIndexName(rs2.getString("INDEX_NAME"));
                index.setType(rs2.getShort("TYPE"));
                index.setOrdinalPosition(rs2.getShort("ORDINAL_POSITION"));
                index.setAscOrDesc(rs2.getString("ASC_OR_DESC"));
                index.setCardinality(rs2.getInt("CARDINALITY"));
                index.getValues().add(col);
                // 索引类型
                if ("PRIMARY".equalsIgnoreCase(indexName) || indexName.equalsIgnoreCase(
                        rsmd.getTableName(1) + "_pkey")) {
                    index.setIndextype(IndexType.PRIMARY);
                } else if (!index.isNonUnique()) {
                    index.setIndextype(IndexType.Unique);
                } else {
                    index.setIndextype(IndexType.Normal);
                }
                tm.getAllIndexes().put(indexName, index);
            }
        }
        // 特殊DB的主键解析
        IndexMeta index = tm.getAllIndexes().get(indexName);
        if (index.getIndextype().value() != 0) {
            if ("H2 JDBC Driver".equals(dbmd.getDriverName())) {
                if (indexName.length() > 11 && "PRIMARY_KEY".equalsIgnoreCase(indexName.substring(0, 11))) {
                    index.setIndextype(IndexType.PRIMARY);
                }
            } else if (dbmd.getDriverName() != null && dbmd.getDriverName().toLowerCase().indexOf("postgresql") >= 0) {
                if ((tableName + "_pkey").equalsIgnoreCase(indexName)) {
                    index.setIndextype(IndexType.PRIMARY);
                }
            }
        }
        allColumns = sortColumns(allColumns, tm.getAllIndexes());
        tm.getAllColumns().putAll(allColumns);
        return tm;
    }

    private static Map<String, ColumnMeta> sortColumns(Map<String, ColumnMeta> allColumns, Map<String, IndexMeta> allIndexes) {
        HashMap<String, ColumnMeta> primaryKeys = new HashMap<>(allColumns.size() / 2);
        HashMap<String, ColumnMeta> simpleColumns = new HashMap<>(allColumns.size());
        for (IndexMeta indexMeta : allIndexes.values()) {
            if (IndexType.PRIMARY.equals(indexMeta.getIndextype())) {
                for (ColumnMeta columnMeta : indexMeta.getValues()) {
                    primaryKeys.put(columnMeta.getColumnName(), columnMeta);
                }
            }
        }

        for (Map.Entry<String, ColumnMeta> entry : allColumns.entrySet()) {
            if (primaryKeys.containsKey(entry.getKey())) {
                continue;
            }
            simpleColumns.put(entry.getKey(), entry.getValue());
        }

        Map<String, ColumnMeta> result = new LinkedHashMap<>(allColumns.size(), 1.0F);
        result.putAll(primaryKeys);
        result.putAll(simpleColumns);
        return result;
    }

}
