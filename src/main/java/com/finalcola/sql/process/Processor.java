package com.finalcola.sql.process;

/**
 * @author: yuanyou.
 * @date: 2019-11-12 11:24
 */
public interface Processor {

    void handle(SqlContext sqlContext);
}
