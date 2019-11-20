package com.finalcola.sql.process;

/**
 * @author: yuanyou.
 * @date: 2019-11-12 11:24
 */
public interface Processor extends Order {

    void handle(SqlContext sqlContext);
}
