package com.finalcola.sql.process.code;

import com.finalcola.sql.config.Configuration;
import com.finalcola.sql.process.AbstractCodeProcessor;
import com.finalcola.sql.process.SqlContext;
import com.finalcola.sql.struct.ClassInfo;
import com.finalcola.sql.struct.TableMeta;
import com.finalcola.sql.util.StringUtils;

import java.util.ArrayList;

/**
 * @author: yuanyou.
 * @date: 2019-11-14 16:19
 */
public class MapperClassProccessor extends AbstractCodeProcessor {

    /**
     * TYPE
     */
    public static final String TYPE = "mapperClass";

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public int getOrder() {
        return 1000;
    }

    @Override
    public void handle(SqlContext sqlContext) {
        ClassInfo classInfo = createClassInfo(sqlContext);
        sqlContext.getClassInfoMap().put(getFullMapperName(sqlContext), classInfo);
    }

    protected ClassInfo createClassInfo(SqlContext sqlContext) {
        Configuration configuration = sqlContext.getConfiguration();
        ClassInfo classInfo = new ClassInfo();
        classInfo.setAccessor("public");
        classInfo.setInterface(true);
        String mapperPkgName = configuration.getMapperPackageName();
        String pkg = configuration.getPackageName() + ((StringUtils.isBlank(mapperPkgName)) ? "" : ("." + mapperPkgName.trim().toLowerCase()));
        classInfo.setPkg(pkg);
        classInfo.setName(getMapperName(sqlContext));

        classInfo.getAnnotations().add("@Repository");
        classInfo.getImports().add("org.springframework.stereotype.Repository");
        classInfo.getImports().add("java.util.List");
        classInfo.getImports().add(getFullEntityName(sqlContext));

        return classInfo;
    }

}
