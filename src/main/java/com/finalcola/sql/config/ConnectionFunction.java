package com.finalcola.sql.config;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author: yuanyou.
 * @date: 2019-11-12 09:43
 */
@FunctionalInterface
public interface ConnectionFunction<R> {

    R apply(Connection connection) throws SQLException;
}
