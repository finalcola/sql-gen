package com.finalcola.sql.spi;

import com.finalcola.sql.anno.ServiceImpl;
import com.finalcola.sql.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author: yuanyou.
 * @date: 2019-11-19 10:11
 */
@Slf4j
public class ServiceLoader<T> {

    /**
     * 加载路径
     */
    public static final String LOAD_DIR = "META-INF/sql-gen/";


    protected static final ConcurrentMap<Class<?>, ServiceLoader<?>> SERVICE_LOADERS = new ConcurrentHashMap<>();

    protected static final ConcurrentMap<Class<?>, Object> INSTANCES = new ConcurrentHashMap<>();

    // ---------

    protected AtomicReference<Map<String, Class<?>>> cachedClasses = new AtomicReference<>();

    private final Class<?> type;

    protected ServiceLoader(Class<?> type) {
        this.type = type;
    }

    public static <T> ServiceLoader<T> getServiceLoader(Class<T> klass){
        if (klass == null) {
            throw new IllegalArgumentException("Extension type == null");
        }
        if (!klass.isInterface()) {
            throw new IllegalArgumentException("Extension type(" + klass + ") is not interface!");
        }
        ServiceLoader<T> serviceLoader = (ServiceLoader<T>) SERVICE_LOADERS.get(klass);
        if (serviceLoader == null) {
            SERVICE_LOADERS.putIfAbsent(klass, new ServiceLoader<>(klass));
            serviceLoader = (ServiceLoader<T>) SERVICE_LOADERS.get(klass);
        }
        return serviceLoader;
    }

    public T getExtension(String name){
        return getExtensions().get(name);
    }

    public Map<String, T> getExtensions() {
        Map<String, Class<?>> extensionClasses = getExtensionClasses();
        HashMap<String, T> result = new HashMap<>(extensionClasses.size(), 1.0F);
        for (Map.Entry<String, Class<?>> entry : extensionClasses.entrySet()) {
            Object instance = INSTANCES.get(entry.getValue());
            if (instance == null) {
                synchronized (INSTANCES) {
                    instance = INSTANCES.get(entry.getValue());
                    if (instance == null) {
                        try {
                            instance = entry.getValue().newInstance();
                            INSTANCES.put(entry.getValue(), instance);
                        } catch (Exception e) {
                            log.error("SPI 加载异常 ==> impl:{}", entry.getValue(), e);
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            result.put(entry.getKey(), (T) instance);
        }
        return result;
    }

    public Map<String, Class<?>> getExtensionClasses() {
        Map<String, Class<?>> extensions = cachedClasses.get();
        if (extensions == null) {
            synchronized (this) {
                extensions = cachedClasses.get();
                if (extensions == null) {
                    extensions = loadExtensionClasses();
                    cachedClasses.set(extensions);
                }
            }
        }
        return extensions;
    }

    private Map<String, Class<?>> loadExtensionClasses() {
        HashMap<String, Class<?>> extensionClasses = new HashMap<>();
        loadFile(extensionClasses, LOAD_DIR);
        return extensionClasses;
    }

    private void loadFile(final HashMap<String, Class<?>> extensionClasses, String loadDir) {
        String fileName = loadDir + type.getName();
        ClassLoader classLoader = getClassLoader();
        Enumeration<URL> urls;
        try {
            if (classLoader != null) {
                urls = classLoader.getResources(fileName);
            } else {
                urls = ClassLoader.getSystemResources(fileName);
            }
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    URL url = urls.nextElement();
                    Files.lines(Paths.get(url.toURI()))
                            .forEach(line -> {
                                // handle read line
                                loadImplInFile(line, extensionClasses);
                            });
                }
            }
        } catch (IOException | URISyntaxException e) {
            log.error("spi load error ==> type:{},fileName:{}", type, fileName, e);
        }
    }

    protected void loadImplInFile(String line, HashMap<String, Class<?>> extensionClasses) {
        if (StringUtils.isBlank(line)) {
            return;
        }
        int docIndex = line.indexOf("#");
        if (docIndex >= 0) {
            line = line.substring(0, docIndex).trim();
        }
        if (line.isEmpty()) {
            return;
        }
        String name = null;
        String impl = null;
        int splitIndex = line.indexOf("=");
        if (splitIndex > 0) {
            impl = line.substring(splitIndex + 1).trim();
            name = line.substring(0, splitIndex).trim();
        } else {
            impl = line;
            name = loadNameByAnnotation();
            if (StringUtils.isBlank(name)) {
                log.error("SPI 配置异常，存在实现类未配置名称:{}", impl);
                return;
            }
        }
        if (impl.isEmpty()) {
            log.error("SPI 配置异常，实现类不能配置为空 ==> name:{},impl:{}", name, impl);
            return;
        }
        try {
            Class<?> implClass = Class.forName(impl);
            if (extensionClasses.get(name) != null) {
                log.error("SPI 配置异常，同一名称对应的实现类重复 ==> name:{},impl:{}", name, impl);
            } else {
                extensionClasses.put(name, implClass);
            }
        } catch (ClassNotFoundException e) {
            log.error("SPI 配置异常，加载实现类失败 ==> name:{},impl:{}", name, impl, e);
            return;
        }
    }

    private String loadNameByAnnotation() {
        ServiceImpl annotation = type.getDeclaredAnnotation(ServiceImpl.class);
        if (annotation == null) {
            return null;
        }
        return annotation.name();
    }

    private ClassLoader getClassLoader() {
        return ServiceLoader.class.getClassLoader();
    }

}
