package com.sussexsoftware.relaxng;


import org.w3c.dom.Document;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

public class StaticValidatorStore implements ValidatorStore {

    private final Map<String, Validator> VAL_MAP = new HashMap<>();
    private XPathExpression XPATH_EXP;

    public StaticValidatorStore(String xpathExpr, NamespaceContext NSC , Path schemaDirectory) throws ValidatorException {

        try {
            // Create XPathFactory object
            XPathFactory xpathFactory = XPathFactory.newInstance();

            // Create XPath object
            XPath xpath = xpathFactory.newXPath();
            xpath.setNamespaceContext(NSC);

            XPATH_EXP = xpath.compile(xpathExpr);

            this.loadValidators(schemaDirectory);

        } catch (Exception exp)
        {
            throw new ValidatorException(exp);
        }

    }

    private void loadValidators(Path directory) throws ValidatorException {

        // for each file in the dirctory with a rng suffix
        // create a validator driver and load the schema
        // map the schema to the file name

        // Lambda consumer that handles the exception from the
        // I/O operation.

        Consumer<Path> consumer = (path)->{

            try {
                InputStream ins = new FileInputStream(path.toFile());

                Validator val = new Validator();
                val.setSchema(path.toString());
                String fileNameWithOutExt =  path.toFile().getName().replaceFirst("[.][^.]+$", "");
                this.VAL_MAP.put(fileNameWithOutExt, val);

            } catch (Exception exp) {

                exp.printStackTrace();
            }
        };

        try {
            Files.list(directory).filter(path -> path.toString().endsWith("rng")).forEach(consumer);
        }catch( IOException exp) {
            throw new ValidatorException("I/O expception while accessing " + directory, exp);
        }
    }

    public Map<String, Validator> getVAL_MAP() {
        return VAL_MAP;
    }


    @SuppressWarnings("SuspiciousMethodCalls")
    public Validator selectValidator(InputStream xmlInStm) throws ValidatorException {

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder;

            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlInStm);

            //evaluate expression result on XML document
            return this.VAL_MAP.get(this.XPATH_EXP.evaluate(doc, XPathConstants.STRING));
        } catch (Exception exp) {
            throw new ValidatorException(exp);
        }
    }

}
