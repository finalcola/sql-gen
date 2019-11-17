package com.finalcola.sql.process;

/**
 * @author: yuanyou.
 * @date: 2019-11-14 15:28
 */
public interface Order {

    default int getOrder() {
        return Integer.MAX_VALUE / 2;
    }
}
