package com.sussexsoftware.relaxng;

import com.sussexsoftware.template.SchemaGenerator;
import com.sussexsoftware.template.SchemaModelGenerator;
import com.sussexsoftware.template.SchemaModelGeneratorFactory;
import com.sussexsoftware.template.TemplateException;
import org.w3c.dom.Document;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class TemplatedValdiatorStore implements ValidatorStore {

    private SchemaGenerator sg;
    private SchemaModelGeneratorFactory smgf;
    private XPathExpression TYPE_XPATH_EXP;

    public TemplatedValdiatorStore(Path templateDirectory) throws ValidatorException {

            try {
                sg = new SchemaGenerator(templateDirectory);

                // Load the Namespace context file
                FileInputStream fs = new FileInputStream(Paths.get(templateDirectory.toString(), "namespaces").toFile());
                Properties ns_props = new Properties();
                ns_props.load(fs);
                NamespaceContext nsc = new SimpleNamespaceContext(ns_props);

                // Load the Type XPath expression and complie the xpath
                fs = new FileInputStream(Paths.get(templateDirectory.toString(), "type").toFile());
                Properties type_props = new Properties();
                type_props.load(fs);
                String type_xpath = type_props.getProperty("type");

                // Create XPathFactory object
                XPathFactory xpathFactory = XPathFactory.newInstance();

                // Create XPath object
                XPath xpath = xpathFactory.newXPath();
                xpath.setNamespaceContext(nsc);
                TYPE_XPATH_EXP = xpath.compile(type_xpath);

                this.smgf = new SchemaModelGeneratorFactory(nsc, templateDirectory);
            } catch (IOException exp) {
                throw new ValidatorException("I/O Expception with reading from tenplate directory files (*.mod, namepsace, type) " + templateDirectory, exp);
            } catch (XPathExpressionException exp) {
                throw new ValidatorException("Unable to complie XPath expressions for one of type (type file) or model (*.mod files) in " + templateDirectory, exp);
            } catch (TemplateException exp) {
                throw new ValidatorException("Unable to create schema generator using files in " + templateDirectory + "expected .ftl files for each message type", exp);
            }
    }


    @Override
    public Validator selectValidator(InputStream xmlInStm) throws ValidatorException {
        Validator val;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlInStm);

            // evaluate expression result on XML document
            String type = this.templateType(doc);

            SchemaModelGenerator smg = smgf.getSchemaModelGenerator(type);

            InputStream is = sg.applyTemplate(type, smg.generateTemplateModel(doc));
            val = new Validator();
            val.setSchema(is);

        } catch (Exception exp) {
            throw new ValidatorException("Unable to select validator due to internal fault", exp);
        }

        return val;
    }


    private String templateType(Document doc) throws XPathExpressionException {
        return  (String) this.TYPE_XPATH_EXP.evaluate(doc, XPathConstants.STRING);
    }

    private class SimpleNamespaceContext implements NamespaceContext {

        private final Map<String, String> PREF_MAP = new HashMap<>();

        SimpleNamespaceContext(Properties namespaces) {

            namespaces.forEach((key,value) -> PREF_MAP.put((String) key, (String) value));
        }


        public String getNamespaceURI(String prefix) {
            return PREF_MAP.get(prefix);
        }

        public String getPrefix(String uri) {
            throw new UnsupportedOperationException();
        }

        public Iterator getPrefixes(String uri) {
            throw new UnsupportedOperationException();
        }
    }

}
