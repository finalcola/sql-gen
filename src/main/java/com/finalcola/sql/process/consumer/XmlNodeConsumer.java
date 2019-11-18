package com.finalcola.sql.process.consumer;

import com.finalcola.sql.config.Configuration;
import com.finalcola.sql.process.SqlContext;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author: yuanyou.
 * @date: 2019-11-14 09:16
 */
@Slf4j
public class XmlNodeConsumer extends AbstractSqlContextConsumer {

    @Override
    public void consume(SqlContext context) {
        long startTime = System.currentTimeMillis();
        Document document = context.getNode().getDocument();
        String xml = printDom(document, false);
//        xml = formatDom(xml);
        long endTime = System.currentTimeMillis();
        log.info("format xml of {} cost {}", context.getTableMeta().getTableName(), (endTime - startTime));
        writeToFile(xml, context);

    }

    protected void writeToFile(String xml, SqlContext context) {
        String filePath = getFilePath(context);
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            // create dir
            if (!file.getParentFile().mkdirs()) {
                throw new RuntimeException("创建文件失败:" + file.getParentFile().getAbsolutePath());
            }

        }
        try {
            // create file
            if (!file.exists() && !file.createNewFile()) {
                throw new RuntimeException("创建文件失败:" + filePath);
            }
            Files.write(Paths.get(filePath), xml.getBytes(StandardCharsets.UTF_8)
                    , StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
            log.info("生成xml文件:{}", filePath);
        } catch (IOException e) {
            log.error("写入xml到文件失败:{}", filePath, e);
        }
    }

    protected String printDom(Document document, boolean pretty) {
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        if (pretty) {
            format.setIndent(true); //设置是否缩进
            format.setNewlines(true); //设置是否换行
        }
        StringWriter stringWriter = new StringWriter();
        XMLWriter xmlWriter = new XMLWriter(stringWriter, format);
        xmlWriter.setEscapeText(false);
        try {
            xmlWriter.write(document);
            xmlWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                xmlWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringWriter.toString();
    }

    protected String formatDom(String xml) {
        try {
            Document document = DocumentHelper.parseText(xml);
            return printDom(document, true);
        } catch (DocumentException e) {
            // should not happen
            throw new RuntimeException("XML解析失败", e);
        }
    }

    protected String getXmlDir(Configuration configuration) {
        return configuration.getDir() + File.separator + "xml";
    }

    protected String getFilePath(SqlContext context) {
        return getXmlDir(context.getConfiguration()) + File.separator + getMapperName(context) + ".xml";
    }

}
