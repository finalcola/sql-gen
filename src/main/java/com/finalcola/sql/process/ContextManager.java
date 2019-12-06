package com.finalcola.sql.process;

import com.finalcola.sql.config.Configuration;
import com.finalcola.sql.process.code.EntityClassProcessor;
import com.finalcola.sql.process.code.MapperClassProccessor;
import com.finalcola.sql.process.consumer.CodeConsumer;
import com.finalcola.sql.process.consumer.SqlContextConsumer;
import com.finalcola.sql.process.consumer.XmlNodeConsumer;
import com.finalcola.sql.process.node.*;
import com.finalcola.sql.struct.TableMeta;
import com.finalcola.sql.struct.TableMetaCache;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: yuanyou.
 * @date: 2019-11-13 18:28
 */
@Slf4j
public class ContextManager {

    private AtomicBoolean started = new AtomicBoolean(false);

    private NodeProcessorChain chain;

    private XmlProcessor xmlProcessor;

    private List<SqlContext> sqlContextList = new ArrayList<>();

    private List<SqlContextConsumer> consumerList;

    private Configuration configuration;

    public ContextManager(Configuration configuration) {
        this.configuration = configuration;
        init();
    }

    public void init(){
        initProcessor();
        initConsumers();
    }

    public void start(){
        if (started.compareAndSet(false, true)) {
            List<String> tableNames = null;
            try {
                tableNames = configuration.getTableNames();
            } catch (SQLException e) {
                throw new RuntimeException("解析数据库异常", e);
            }
            if (tableNames != null) {
                for (String tableName : tableNames) {
                    SqlContext sqlContext = createSqlContext(configuration, tableName);
                    processSqlContext(sqlContext);
                    consume(sqlContext);
                    sqlContextList.add(sqlContext);
                }
            }
        }
    }

    protected void consume(SqlContext sqlContext) {
        for (SqlContextConsumer consumer : consumerList) {
            consumer.consume(sqlContext);
        }
    }

    protected SqlContext processSqlContext(SqlContext sqlContext) {
        xmlProcessor.handle(sqlContext);
        sqlContext.merge();
        return sqlContext;
    }

    protected SqlContext createSqlContext(Configuration configuration, String tableName) {
        SqlContext context = new SqlContext();
        context.setConfiguration(configuration);
        DataSource dataSource = configuration.getDataSource();
        TableMeta tableMeta = TableMetaCache.getTableMeta(tableName, dataSource);
        context.setTableMeta(tableMeta);
        return context;
    }

    public void initProcessor(){
        chain = new NodeProcessorChain();
        initChain();
        xmlProcessor = new XmlProcessor(chain);
    }

    public void initConsumers(){
        consumerList = new ArrayList<>();
        consumerList.add(new XmlNodeConsumer());
        consumerList.add(new CodeConsumer());
    }

    protected void initChain() {
        chain.addProcessor(new ResultMapProcessor());
        chain.addProcessor(new BaseColumnSqlProcessor());
        chain.addProcessor(new InsertProcessor());
        chain.addProcessor(new InsertBatchProcessor());
        chain.addProcessor(new DeleteProcessor());
        chain.addProcessor(new SelectByPrimaryKeyProcessor());
        chain.addProcessor(new SelectByParamProcessor());
        chain.addProcessor(new SelectCountByParamProcessor());
        chain.addProcessor(new UpdateProcessor());

        chain.addProcessor(new EntityClassProcessor());
        chain.addProcessor(new MapperClassProccessor());
    }
}
