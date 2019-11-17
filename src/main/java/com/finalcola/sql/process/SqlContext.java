package com.finalcola.sql.process;

import com.finalcola.sql.config.Configuration;
import com.finalcola.sql.struct.ClassInfo;
import com.finalcola.sql.struct.TableMeta;
import com.finalcola.sql.util.CollectionUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author: yuanyou.
 * @date: 2019-11-12 11:30
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@Slf4j
public class SqlContext {

    /**
     * 模板文件默认目录（classPath）
     */
    public static final String TEMPLATE_DIR = "template";

    protected Configuration configuration;

    private List<SqlContext> sub = Collections.synchronizedList(new ArrayList<>());

    private Map<String, ClassInfo> classInfoMap = new HashMap<>();

    private Element node;

    private Map<String, String> params;

    private List<Consumer<SqlContext>> mergeFunList = new ArrayList<>();

    private TableMeta tableMeta;

    public void merge(){
        if (CollectionUtils.isNotEmpty(mergeFunList)) {
            for (int i = 0; i < mergeFunList.size(); i++) {
                mergeFunList.get(i).accept(this);
            }
        }
    }

    public SqlContext addMergeFun(Consumer<SqlContext> mergeFun) {
        if (mergeFun == null) {
            throw new RuntimeException("mergeFun should not be null");
        }
        mergeFunList.add(mergeFun);
        return this;
    }

    public SqlContext addSub(SqlContext sqlContext){
        sub.add(sqlContext);
        return this;
    }

    private ClassLoader getClassLoader(){
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader != null) {
            return classLoader;
        }
        classLoader = this.getClass().getClassLoader();
        return classLoader;
    }

}
