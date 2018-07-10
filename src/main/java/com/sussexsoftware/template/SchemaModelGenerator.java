package com.sussexsoftware.template;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SchemaModelGenerator {

    private final Map<String,XPathExpression> MODEL_XPATH_MAP = new HashMap<>();

    public SchemaModelGenerator(Properties model_xpaths , NamespaceContext NSC ) throws TemplateException {

        ArrayList<Exception> errors = new ArrayList<>();

        // Create XPathFactory object
        XPathFactory xpathFactory = XPathFactory.newInstance();

        // Create XPath object
        XPath xpath = xpathFactory.newXPath();
        xpath.setNamespaceContext(NSC);

        model_xpaths.forEach((key, val) -> {

                    XPath modelXPath = xpathFactory.newXPath();
                    xpath.setNamespaceContext(NSC);
                    try {
                        MODEL_XPATH_MAP.put((String) key, modelXPath.compile((String) val));
                    } catch (XPathExpressionException exp) {
                       errors.add(exp);
                    }
                }
        );

        if (!errors.isEmpty()) throw new TemplateException("Unable to proces XPath Expressions", errors.get(0));

    }

    public Map<String, Object> generateTemplateModel(Document doc) throws TemplateException {

        ArrayList<Exception> errors = new ArrayList<>();
        Map<String, Object> vals = new HashMap<>();

        MODEL_XPATH_MAP.forEach((key,val) -> {

            ArrayList<String> list = new ArrayList<>();

            NodeList nodeset;
            try {
                nodeset = (NodeList) val.evaluate(doc, XPathConstants.NODESET);

                for (int ii = 0; ii < nodeset.getLength(); ii++) {
                    Node node = nodeset.item(ii);
                    list.add(node.getTextContent());
                }

                vals.put(key,list);

            } catch (XPathExpressionException exp) {
                errors.add(exp);
            }

        });

        if (!errors.isEmpty()) throw new TemplateException("Unable to generate Template model", errors.get(0));

        return vals;
    }

}
