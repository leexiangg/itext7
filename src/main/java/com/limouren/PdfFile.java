package com.limouren;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.font.FontProvider;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 生成PDF文件
 */
public class PdfFile {

    /**
     * 填充html模板
     * @param templateFile 模板文件名
     * @param args 模板参数
     * @param pdfFile 生成文件路径
     */
    public static void template(String templateFile, Map<String, String> args, String pdfFile) {
        FileOutputStream output = null;
        try {
            // 读取模板文件,填充模板参数
            Configuration freemarkerCfg = new Configuration(Configuration.VERSION_2_3_30);
            freemarkerCfg.setTemplateLoader(new ClassTemplateLoader(PdfFile.class, "/template/"));
            Template template = freemarkerCfg.getTemplate(templateFile);
            StringWriter out = new StringWriter();
            if (args != null && args.size() > 0)
                template.process(args, out);
            String html = out.toString();

            // 设置字体以及字符编码
            ConverterProperties props = new ConverterProperties();
            FontProvider fontProvider = new FontProvider();
            PdfFont sysFont = PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H", false);
            fontProvider.addFont(sysFont.getFontProgram(), "UniGB-UCS2-H");
            fontProvider.addStandardPdfFonts();
            fontProvider.addFont("template/simsun.ttc");
            fontProvider.addFont("template/STHeitibd.ttf");
            props.setFontProvider(fontProvider);
            props.setCharset("utf-8");

            // 转换为PDF文档
            if(pdfFile.indexOf("/") > 0) {
                File path = new File(pdfFile.substring(0, pdfFile.indexOf("/")));
                if (!path.exists())
                    path.mkdirs();
            }
            output = new FileOutputStream(new File(pdfFile));
            PdfDocument pdf = new PdfDocument(new PdfWriter(output));
            pdf.setDefaultPageSize(PageSize.A4);
            Document document = HtmlConverter.convertToDocument(html, pdf, props);
            document.getRenderer().close();
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(output != null)
                    output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Map<String, String> para = new HashMap<String, String>();
        para.put("var1", "这个值是填充的变量");
        para.put("tab1", "<tr><td>1</td><td>第一个项目</td><td>第一个项目的具体内容</td></tr><tr><td>2</td><td>第二个项目</td><td>第二个项目的具体内容</td></tr>");
        template("template.html", para, "tmp/" + new Date().getTime() + ".pdf");
    }

}
