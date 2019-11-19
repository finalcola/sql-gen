package com.finalcola.sql;

import com.finalcola.sql.config.Configuration;
import com.finalcola.sql.process.ContextManager;

/**
 * bootstrap
 * @author: yuanyou.
 * @date: 2019-11-19 09:57
 */
public class SqlGen {

    public static void generateSql(Configuration configuration){
        ContextManager contextManager = new ContextManager(configuration);
        contextManager.start();
    }
}
