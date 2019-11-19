package com.finalcola.sql.spi;

import com.finalcola.sql.process.Processor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author: yuanyou.
 * @date: 2019-11-19 17:20
 */
@Slf4j
public class ServiceLoaderTest {

    @Test
    public void getExtensionClasses() {
        ServiceLoader<Processor> serviceLoader = ServiceLoader.getServiceLoader(Processor.class);
        Map<String, Class<?>> extensionClasses = serviceLoader.getExtensionClasses();
        assert !extensionClasses.isEmpty();
        log.info("result:{}", extensionClasses);
    }

    @Test
    public void getExtension() {
        ServiceLoader<Processor> serviceLoader = ServiceLoader.getServiceLoader(Processor.class);
        Processor processor = serviceLoader.getExtension("insert");
        assert processor != null;
    }
}