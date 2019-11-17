package com.finalcola.sql.process.consumer;

import com.finalcola.sql.process.SqlContext;

/**
 * @author: yuanyou.
 * @date: 2019-11-14 09:11
 */
public interface SqlContextConsumer {

    void consume(SqlContext context);
}
