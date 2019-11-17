package com.finalcola.sql.process.consumer;

import com.finalcola.sql.config.Configuration;
import com.finalcola.sql.process.SqlContext;
import com.finalcola.sql.struct.ClassInfo;
import com.finalcola.sql.struct.FieldInfo;
import com.finalcola.sql.struct.MethodInfo;
import com.finalcola.sql.struct.ParamInfo;
import com.finalcola.sql.util.CollectionUtils;
import com.finalcola.sql.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;

/**
 * @author: yuanyou.
 * @date: 2019-11-14 16:41
 */
@Slf4j
public class CodeConsumer extends AbstractSqlContextConsumer {


    @Override
    public void consume(SqlContext context) {
        Map<String, ClassInfo> classInfoMap = context.getClassInfoMap();
        for (ClassInfo classInfo : classInfoMap.values()) {
            long startTime = System.currentTimeMillis();
            String code = generateCode(classInfo, context.getConfiguration());
            String filePath = getFilePath(classInfo, context.getConfiguration());
            writeToFile(code, filePath);
            long endTime = System.currentTimeMillis();
            log.debug("generate {} cost {}", filePath, (endTime - startTime));
        }
    }

    protected String getFilePath(ClassInfo classInfo, Configuration configuration) {
        String pkg = classInfo.getPkg();
        if (pkg != null) {
            pkg = pkg.replaceAll("\\.", Matcher.quoteReplacement(File.separator)) + File.separator;
        } else {
            pkg = "";
        }
        return configuration.getDir() + File.separator + pkg + classInfo.getName() + ".java";
    }

    protected void writeToFile(String content, String filePath) {
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                throw new RuntimeException("创建目录失败:" + file.getParentFile().getAbsolutePath());
            }
        }
        try {
            Files.write(Paths.get(filePath), content.getBytes(StandardCharsets.UTF_8)
                    , StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (IOException e) {
            log.error("写入文件失败:{}", filePath, e);
            throw new RuntimeException("写入文件失败:" + e);
        }
    }

    protected String generateCode(ClassInfo classInfo, Configuration configuration) {
        StringBuilder builder = new StringBuilder();
        generatePackage(classInfo, builder);
        generateImports(classInfo, builder);
        generateClassHead(classInfo, builder);
        generateFields(classInfo, builder);
        generateMethods(classInfo, builder);
        generateClassTail(classInfo, builder);
        return builder.toString();
    }

    protected void generatePackage(ClassInfo classInfo, StringBuilder builder) {
        String pkg = classInfo.getPkg();
        if (StringUtils.isNotBlank(pkg)) {
            builder.append("package ").append(pkg.trim()).append(";\n");
        }
    }

    protected void generateImports(ClassInfo classInfo, StringBuilder builder) {
        Set<String> imports = classInfo.getImports();
        if (!imports.isEmpty()) {
            for (String importClass : imports) {
                builder.append("import ").append(importClass).append(";\n");
            }
        }
        builder.append("\n");
    }

    protected void generateClassHead(ClassInfo classInfo, StringBuilder builder) {
        for (String annotation : classInfo.getAnnotations()) {
            builder.append(annotation).append("\n");
        }
        builder.append(classInfo.getAccessor());
        if (classInfo.isInterface()) {
            builder.append(" ").append("interface ");
        } else {
            builder.append(" ").append("class ");
        }
        builder.append(classInfo.getName());
        builder.append(" {");
    }

    protected void generateFields(ClassInfo classInfo, StringBuilder builder) {
        String indent = "    ";
        List<FieldInfo> fieldInfos = classInfo.getFieldInfos();
        for (FieldInfo fieldInfo : fieldInfos) {
            builder.append("\n");
            if (StringUtils.isNotBlank(fieldInfo.getDoc())) {
                generateDoc(fieldInfo.getDoc(), indent, builder);
            }
            for (String annotation : fieldInfo.getAnnotations()) {
                builder.append(indent).append(annotation).append("\n");
            }
            builder.append(indent)
                    .append(fieldInfo.getAccessor())
                    .append(" ")
                    .append(fieldInfo.getType())
                    .append(" ")
                    .append(fieldInfo.getName())
                    .append(";");
        }
    }

    protected void generateMethods(ClassInfo classInfo, StringBuilder builder) {
        String indent = "    ";
        List<MethodInfo> methodInfos = classInfo.getMethodInfos();
        for (MethodInfo methodInfo : methodInfos) {
            builder.append("\n");
            if (StringUtils.isNotBlank(methodInfo.getDoc())) {
                generateDoc(methodInfo.getDoc(), indent, builder);
            }
            Set<String> annotaions = methodInfo.getAnnotaions();
            for (String annotaion : annotaions) {
                builder.append(indent).append(annotaion).append("\n");
            }
            builder.append(indent);
            if (!methodInfo.getIsInterface()) {
                if (StringUtils.isNotBlank(methodInfo.getAccessor())) {
                    builder.append(methodInfo.getAccessor()).append(" ");
                }
                if (methodInfo.getIsAbstract()) {
                    builder.append("abstarct ");
                }
            }
            builder.append(methodInfo.getReturnType()==null?"void":methodInfo.getReturnType())// TODO: 2019-11-14 泛型返回值（class代码也需要支持）
                    .append(" ")
                    .append(methodInfo.getName())
                    .append("(")
                    .append(generateParams(methodInfo))
                    .append(")");
            String exceptionStr = methodInfo.getExceptions().stream()
                    .reduce((e1, e2) -> e1 + ", " + e2)
                    .map(str -> "throws " + str)
                    .orElse("");
            builder.append(exceptionStr);
            if (methodInfo.getIsInterface() || methodInfo.getIsAbstract()) {
                builder.append(";");
            } else {
                builder.append(" {");
                if (methodInfo.getCodes().size() > 0) {
                    builder.append("\n");
                }
                for (String code : methodInfo.getCodes()) {
                    builder.append(indent).append(indent).append(code);
                    if (!code.endsWith(";")) {
                        builder.append(";");
                    }
                    builder.append("\n");
                }
                builder.append("}");

            }
        }
    }

    private String generateParams(MethodInfo methodInfo) {
        List<ParamInfo> paramInfos = methodInfo.getParamInfos();
        if (paramInfos.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (ParamInfo paramInfo : paramInfos) {
            String annoStr = paramInfo.getAnnotaions().stream()
                    .reduce((s1, s2) -> s1 + ", " + s2)
                    .map(s -> s + " ")
                    .orElse("");

            builder.append(annoStr);
            builder.append(paramInfo.getType()).append(" ").append(paramInfo.getName());
            builder.append(", ");
        }
        return builder.substring(0, builder.length() - 2);
    }

    protected void generateDoc(String doc, String indent, StringBuilder builder) {
        builder.append(indent).append("/**").append("\n");
        builder.append(indent).append(" * ").append(doc).append("\n");
        builder.append(indent).append(" */").append("\n");
    }

    protected void generateClassTail(ClassInfo classInfo, StringBuilder builder) {
        builder.append("\n}");
    }

}
