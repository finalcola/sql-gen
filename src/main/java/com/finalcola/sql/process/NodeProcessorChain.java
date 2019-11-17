package com.finalcola.sql.process;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * @author: yuanyou.
 * @date: 2019-11-12 14:39
 */
@NoArgsConstructor
@Data
@Slf4j
public class NodeProcessorChain implements Processor {

    PriorityQueue<AbstractProcessor> nodeProcessors = new PriorityQueue<>(Comparator.comparingInt(Order::getOrder));

    @Override
    public void handle(SqlContext sqlContext) {
        if (sqlContext == null) {
            return;
        }
        Set<String> excludeNodes = sqlContext.getConfiguration().getExcludeNodes();
        Iterator<AbstractProcessor> iterator = nodeProcessors.iterator();
        while (iterator.hasNext()) {
            AbstractProcessor next = iterator.next();
            if (next == null || excludeNodes.contains(next.getType())) {
                continue;
            }
            long start = System.currentTimeMillis();
            next.handle(sqlContext);
            long endTime = System.currentTimeMillis();
            log.debug("processor {} cost {}", next.getClass(), (endTime - start));
        }
    }

    public NodeProcessorChain addProcessor(AbstractProcessor processor){
        nodeProcessors.add(processor);
        return this;
    }
}
