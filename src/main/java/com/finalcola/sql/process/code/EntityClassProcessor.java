package com.finalcola.sql.process.code;

import com.finalcola.sql.config.Configuration;
import com.finalcola.sql.process.AbstractCodeProcessor;
import com.finalcola.sql.process.SqlContext;
import com.finalcola.sql.struct.*;
import com.finalcola.sql.util.MysqlTypeMap;
import com.finalcola.sql.util.StringUtils;

import java.util.Map;

/**
 * @author: yuanyou.
 * @date: 2019-11-14 17:49
 */
public class EntityClassProcessor extends AbstractCodeProcessor {

    @Override
    public String getType() {
        return "entityClass";
    }

    @Override
    public void handle(SqlContext sqlContext) {
        ClassInfo classInfo = createClassInfo(sqlContext);
        sqlContext.getClassInfoMap().put(getFullEntityName(sqlContext), classInfo);
    }

    protected ClassInfo createClassInfo(SqlContext sqlContext) {
        ClassInfo classInfo = new ClassInfo();
        classInfo.setName(getEntityName(sqlContext));
        Configuration configuration = sqlContext.getConfiguration();
        String entityPkgName = configuration.getEntityPackageName();
        String pkg = configuration.getPackageName() + (StringUtils.isBlank(entityPkgName) ? "" : "." + entityPkgName.trim().toLowerCase());
        classInfo.setPkg(pkg);
        classInfo.setInterface(false);
        classInfo.setAccessor("public");
        classInfo.setSuperClass(configuration.getEntityParentClass());
        if (configuration.isSupportLombok()) {
            classInfo.getAnnotations().add("@Data");
            classInfo.getImports().add("lombok.Data");
        }
        processImports(classInfo, sqlContext);
        processFields(classInfo, sqlContext);
        if (!configuration.isSupportLombok()) {
            processGetterAndSetter(classInfo, sqlContext);
        }

        return classInfo;
    }

    private void processImports(ClassInfo classInfo, SqlContext sqlContext) {
        sqlContext.getTableMeta().getAllColumns().values().stream()
                .filter(columnMeta -> MysqlTypeMap.isNotBasicType(columnMeta.getDataTypeName()))
                .map(columnMeta -> MysqlTypeMap.getFullJavaType(columnMeta.getDataTypeName()))
                .forEach(javaType -> classInfo.getImports().add(javaType));
    }

    protected void processFields(ClassInfo classInfo, SqlContext sqlContext) {
        for (Map.Entry<String, ColumnMeta> entry : sqlContext.getTableMeta().getAllColumns().entrySet()) {
            String column = entry.getKey();
            ColumnMeta columnMeta = entry.getValue();
            String fieldName = StringUtils.toCamel(column, false);
            FieldInfo fieldInfo = new FieldInfo();
            fieldInfo.setName(fieldName);
            fieldInfo.setDoc(columnMeta.getRemarks());
            fieldInfo.setType(MysqlTypeMap.getJavaType(columnMeta.getDataTypeName()));
            fieldInfo.setAccessor("private");
            classInfo.getFieldInfos().add(fieldInfo);
        }
    }

    private void processGetterAndSetter(ClassInfo classInfo, SqlContext sqlContext) {
        Map<String, ColumnMeta> allColumns = sqlContext.getTableMeta().getAllColumns();
        for (Map.Entry<String, ColumnMeta> entry : allColumns.entrySet()) {
            MethodInfo getter = createGetter(entry.getKey(), entry.getValue(), sqlContext);
            MethodInfo setter = createSetter(entry.getKey(), entry.getValue(), sqlContext);
            classInfo.getMethodInfos().add(getter);
            classInfo.getMethodInfos().add(setter);
        }
    }

    private MethodInfo createGetter(String column, ColumnMeta columnMeta, SqlContext context) {
        MethodInfo methodInfo = new MethodInfo();
        String fieldName = StringUtils.toCamel(column, false);
        String methodName = "get" + StringUtils.toCamel(column, true);
        methodInfo.setName(methodName);
        methodInfo.setReturnType(MysqlTypeMap.getJavaType(columnMeta.getDataTypeName()));
        methodInfo.setIsGeneric(false);
        methodInfo.setAccessor("public");
        methodInfo.getCodes().add("return " + fieldName + ";");
        methodInfo.setIsAbstract(false);
        methodInfo.setIsInterface(false);
        return methodInfo;
    }

    private MethodInfo createSetter(String column, ColumnMeta columnMeta, SqlContext context) {
        MethodInfo methodInfo = new MethodInfo();
        String fieldName = StringUtils.toCamel(column, false);
        String methodName = "set" + StringUtils.toCamel(column, true);
        methodInfo.setName(methodName);
        methodInfo.setIsGeneric(false);
        methodInfo.setAccessor("public");
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.setType(MysqlTypeMap.getJavaType(columnMeta.getDataTypeName()));
        paramInfo.setName(fieldName);
        methodInfo.getParamInfos().add(paramInfo);
        methodInfo.getCodes().add(String.format("this.%s = %s;", fieldName, fieldName));
        methodInfo.setIsAbstract(false);
        methodInfo.setIsInterface(false);
        return methodInfo;
    }
}