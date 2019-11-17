package com.finalcola.sql.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author: yuanyou.
 * @date: 2019-11-12 17:44
 */
public final class MysqlKeywordUtils {

    public static final Set<String> KEYWORDS = new HashSet<>();

    private MysqlKeywordUtils() {

    }

    public static String processKeyword(String word) {
        if (isKeyword(word)) {
            return "`" + word + "`";
        }
        return word;
    }

    public static boolean isKeyword(String word){
        if (word == null) {
            return false;
        }
        return KEYWORDS.contains(word.toUpperCase());
    }

    static {
        KEYWORDS.add("ADD");
        KEYWORDS.add("ALL");
        KEYWORDS.add("ALTER");
        KEYWORDS.add("ANALYZE");
        KEYWORDS.add("AND");
        KEYWORDS.add("AS");
        KEYWORDS.add("ASC");
        KEYWORDS.add("ASENSITIVE");
        KEYWORDS.add("BEFORE");
        KEYWORDS.add("BETWEEN");
        KEYWORDS.add("BIGINT");
        KEYWORDS.add("BINARY");
        KEYWORDS.add("BLOB");
        KEYWORDS.add("BOTH");
        KEYWORDS.add("BY");
        KEYWORDS.add("CALL");
        KEYWORDS.add("CASCADE");
        KEYWORDS.add("CASE");
        KEYWORDS.add("CHANGE");
        KEYWORDS.add("CHAR");
        KEYWORDS.add("CHARACTER");
        KEYWORDS.add("CHECK");
        KEYWORDS.add("COLLATE");
        KEYWORDS.add("COLUMN");
        KEYWORDS.add("CONDITION");
        KEYWORDS.add("CONNECTION");
        KEYWORDS.add("CONSTRAINT");
        KEYWORDS.add("CONTINUE");
        KEYWORDS.add("CONVERT");
        KEYWORDS.add("CREATE");
        KEYWORDS.add("CROSS");
        KEYWORDS.add("CURRENT_DATE");
        KEYWORDS.add("CURRENT_TIME");
        KEYWORDS.add("CURRENT_TIMESTAMP");
        KEYWORDS.add("CURRENT_USER");
        KEYWORDS.add("CURSOR");
        KEYWORDS.add("DATABASE");
        KEYWORDS.add("DATABASES");
        KEYWORDS.add("DAY_HOUR");
        KEYWORDS.add("DAY_MICROSECOND");
        KEYWORDS.add("DAY_MINUTE");
        KEYWORDS.add("DAY_SECOND");
        KEYWORDS.add("DEC");
        KEYWORDS.add("DECIMAL");
        KEYWORDS.add("DECLARE");
        KEYWORDS.add("DEFAULT");
        KEYWORDS.add("DELAYED");
        KEYWORDS.add("DELETE");
        KEYWORDS.add("DESC");
        KEYWORDS.add("DESCRIBE");
        KEYWORDS.add("DETERMINISTIC");
        KEYWORDS.add("DISTINCT");
        KEYWORDS.add("DISTINCTROW");
        KEYWORDS.add("DIV");
        KEYWORDS.add("DOUBLE");
        KEYWORDS.add("DROP");
        KEYWORDS.add("DUAL");
        KEYWORDS.add("EACH");
        KEYWORDS.add("ELSE");
        KEYWORDS.add("ELSEIF");
        KEYWORDS.add("ENCLOSED");
        KEYWORDS.add("ESCAPED");
        KEYWORDS.add("EXISTS");
        KEYWORDS.add("EXIT");
        KEYWORDS.add("EXPLAIN");
        KEYWORDS.add("FALSE");
        KEYWORDS.add("FETCH");
        KEYWORDS.add("FLOAT");
        KEYWORDS.add("FLOAT4");
        KEYWORDS.add("FLOAT8");
        KEYWORDS.add("FOR");
        KEYWORDS.add("FORCE");
        KEYWORDS.add("FOREIGN");
        KEYWORDS.add("FROM");
        KEYWORDS.add("FULLTEXT");
        KEYWORDS.add("GOTO");
        KEYWORDS.add("GRANT");
        KEYWORDS.add("GROUP");
        KEYWORDS.add("HAVING");
        KEYWORDS.add("HIGH_PRIORITY");
        KEYWORDS.add("HOUR_MICROSECOND");
        KEYWORDS.add("HOUR_MINUTE");
        KEYWORDS.add("HOUR_SECOND");
        KEYWORDS.add("IF");
        KEYWORDS.add("IGNORE");
        KEYWORDS.add("IN");
        KEYWORDS.add("INDEX");
        KEYWORDS.add("INFILE");
        KEYWORDS.add("INNER");
        KEYWORDS.add("INOUT");
        KEYWORDS.add("INSENSITIVE");
        KEYWORDS.add("INSERT");
        KEYWORDS.add("INT");
        KEYWORDS.add("INT1");
        KEYWORDS.add("INT2");
        KEYWORDS.add("INT3");
        KEYWORDS.add("INT4");
        KEYWORDS.add("INT8");
        KEYWORDS.add("INTEGER");
        KEYWORDS.add("INTERVAL");
        KEYWORDS.add("INTO");
        KEYWORDS.add("IS");
        KEYWORDS.add("ITERATE");
        KEYWORDS.add("JOIN");
        KEYWORDS.add("KEY");
        KEYWORDS.add("KEYS");
        KEYWORDS.add("KILL");
        KEYWORDS.add("LABEL");
        KEYWORDS.add("LEADING");
        KEYWORDS.add("LEAVE");
        KEYWORDS.add("LEFT");
        KEYWORDS.add("LIKE");
        KEYWORDS.add("LIMIT");
        KEYWORDS.add("LINEAR");
        KEYWORDS.add("LINES");
        KEYWORDS.add("LOAD");
        KEYWORDS.add("LOCALTIME");
        KEYWORDS.add("LOCALTIMESTAMP");
        KEYWORDS.add("LOCK");
        KEYWORDS.add("LONG");
        KEYWORDS.add("LONGBLOB");
        KEYWORDS.add("LONGTEXT");
        KEYWORDS.add("LOOP");
        KEYWORDS.add("LOW_PRIORITY");
        KEYWORDS.add("MATCH");
        KEYWORDS.add("MEDIUMBLOB");
        KEYWORDS.add("MEDIUMINT");
        KEYWORDS.add("MEDIUMTEXT");
        KEYWORDS.add("MIDDLEINT");
        KEYWORDS.add("MINUTE_MICROSECOND");
        KEYWORDS.add("MINUTE_SECOND");
        KEYWORDS.add("MOD");
        KEYWORDS.add("MODIFIES");
        KEYWORDS.add("NATURAL");
        KEYWORDS.add("NOT");
        KEYWORDS.add("NO_WRITE_TO_BINLOG");
        KEYWORDS.add("NULL");
        KEYWORDS.add("NUMERIC");
        KEYWORDS.add("ON");
        KEYWORDS.add("OPTIMIZE");
        KEYWORDS.add("OPTION");
        KEYWORDS.add("OPTIONALLY");
        KEYWORDS.add("OR");
        KEYWORDS.add("ORDER");
        KEYWORDS.add("OUT");
        KEYWORDS.add("OUTER");
        KEYWORDS.add("OUTFILE");
        KEYWORDS.add("PRECISION");
        KEYWORDS.add("PRIMARY");
        KEYWORDS.add("PROCEDURE");
        KEYWORDS.add("PURGE");
        KEYWORDS.add("RAID0");
        KEYWORDS.add("RANGE");
        KEYWORDS.add("READ");
        KEYWORDS.add("READS");
        KEYWORDS.add("REAL");
        KEYWORDS.add("REFERENCES");
        KEYWORDS.add("REGEXP");
        KEYWORDS.add("RELEASE");
        KEYWORDS.add("RENAME");
        KEYWORDS.add("REPEAT");
        KEYWORDS.add("REPLACE");
        KEYWORDS.add("REQUIRE");
        KEYWORDS.add("RESTRICT");
        KEYWORDS.add("RETURN");
        KEYWORDS.add("REVOKE");
        KEYWORDS.add("RIGHT");
        KEYWORDS.add("RLIKE");
        KEYWORDS.add("SCHEMA");
        KEYWORDS.add("SCHEMAS");
        KEYWORDS.add("SECOND_MICROSECOND");
        KEYWORDS.add("SELECT");
        KEYWORDS.add("SENSITIVE");
        KEYWORDS.add("SEPARATOR");
        KEYWORDS.add("SET");
        KEYWORDS.add("SHOW");
        KEYWORDS.add("SMALLINT");
        KEYWORDS.add("SPATIAL");
        KEYWORDS.add("SPECIFIC");
        KEYWORDS.add("SQL");
        KEYWORDS.add("SQLEXCEPTION");
        KEYWORDS.add("SQLSTATE");
        KEYWORDS.add("SQLWARNING");
        KEYWORDS.add("SQL_BIG_RESULT");
        KEYWORDS.add("SQL_CALC_FOUND_ROWS");
        KEYWORDS.add("SQL_SMALL_RESULT");
        KEYWORDS.add("SSL");
        KEYWORDS.add("STARTING");
        KEYWORDS.add("STRAIGHT_JOIN");
        KEYWORDS.add("STATUS");
        KEYWORDS.add("TABLE");
        KEYWORDS.add("TERMINATED");
        KEYWORDS.add("THEN");
        KEYWORDS.add("TINYBLOB");
        KEYWORDS.add("TINYINT");
        KEYWORDS.add("TINYTEXT");
        KEYWORDS.add("TO");
        KEYWORDS.add("TRAILING");
        KEYWORDS.add("TRIGGER");
        KEYWORDS.add("TRUE");
        KEYWORDS.add("UNDO");
        KEYWORDS.add("UNION");
        KEYWORDS.add("UNIQUE");
        KEYWORDS.add("UNLOCK");
        KEYWORDS.add("UNSIGNED");
        KEYWORDS.add("UPDATE");
        KEYWORDS.add("USAGE");
        KEYWORDS.add("USE");
        KEYWORDS.add("USING");
        KEYWORDS.add("UTC_DATE");
        KEYWORDS.add("UTC_TIME");
        KEYWORDS.add("UTC_TIMESTAMP");
        KEYWORDS.add("VALUES");
        KEYWORDS.add("VARBINARY");
        KEYWORDS.add("VARCHAR");
        KEYWORDS.add("VARCHARACTER");
        KEYWORDS.add("VARYING");
        KEYWORDS.add("WHEN");
        KEYWORDS.add("WHERE");
        KEYWORDS.add("WHILE");
        KEYWORDS.add("WITH");
        KEYWORDS.add("WRITE");
        KEYWORDS.add("X509");
        KEYWORDS.add("XOR");
        KEYWORDS.add("YEAR_MONTH");
        KEYWORDS.add("ZEROFILL");
    }


}
