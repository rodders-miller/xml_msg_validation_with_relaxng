package com.sussexsoftware.template;

import freemarker.template.*;
import java.io.*;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;

public class SchemaGenerator {

    private Configuration freeMarkerCfg;

    public  SchemaGenerator(Path dir) throws TemplateException {

        try {
            Configuration cfg = new Configuration(new Version(2, 3, 20));

            // Some other recommended settings:
            cfg.setDefaultEncoding("UTF-8");
            cfg.setLocale(Locale.US);
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

            cfg.setDirectoryForTemplateLoading(dir.toFile());

            freeMarkerCfg = cfg;
        } catch (IOException exp) {
            throw new TemplateException("Unable to read template dir " + dir, exp);
        }
    }

    public InputStream applyTemplate(String name, Map<String, Object> input) throws TemplateException {

        String templateName = name + ".ftl";

        try {
            Template template = this.freeMarkerCfg.getTemplate(templateName);

            // Write output to the console
            ByteArrayOutputStream outstr = new ByteArrayOutputStream();
            Writer consoleWriter = new OutputStreamWriter(outstr);
            template.process(input, consoleWriter);

            return new ByteArrayInputStream(outstr.toByteArray());
        } catch (IOException exp) {
            throw new TemplateException("Unable to read template file " + templateName, exp);
        } catch (freemarker.template.TemplateException exp) {
            throw new TemplateException("Unable to apply template", exp);
        }
    }

}
