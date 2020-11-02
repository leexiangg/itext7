@[TOC](目录)

代码：[https://github.com/leexiangg/itext7](https://github.com/leexiangg/itext7)

itext7官方参考文档：[https://kb.itextpdf.com/home/it7kb](https://kb.itextpdf.com/home/it7kb)

# 1、准备html格式的模板
1. 可以先把需要生成的文档只作为word，生成html格式，推荐的在线导出网站：[http://www.docpe.com/word/word-to-html.aspx](http://www.docpe.com/word/word-to-html.aspx)
2. 在ue中格式化html后，精简优化html代码，删除多余标签、空格、空行等。
3. 把“黑体”或者“加粗”替换为“STHeiti”，把“宋体”替换为“SimSun”。
4. 表格用百分比设置宽度。
5. 增加页数css配置。
6. 文档中长度单位最好使用pt。


# 2、在项目中引入包
``` xml
<!-- 生成html模板 -->
<dependency>
    <groupId>org.freemarker</groupId>
    <artifactId>freemarker</artifactId>
    <version>2.3.30</version>
</dependency>
<!--itext7 html转pdf用到的包-->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>html2pdf</artifactId>
    <version>3.0.1</version>
</dependency>
<!--itext7 中文支持-->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>font-asian</artifactId>
    <version>7.1.12</version>
</dependency>
<!--日志支持-->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-simple</artifactId>
    <version>1.7.30</version>
</dependency>
```


# 3、代码实现

``` java
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
```



# 4、注意事项
1. 字体文件 simsun.ttc、STHeitibd.ttf 请从 [github代码](https://github.com/leexiangg/itext7) 中下载。
2. 如果有问题，可以到 [itext7]([https://kb.itextpdf.com/home/it7kb](https://kb.itextpdf.com/home/it7kb)) 官方文档中查看示例。

