package com.sussexsoftware.relaxng;


import org.junit.jupiter.api.Test;
import javax.xml.namespace.NamespaceContext;
import java.io.FileInputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorStoreTest {

    private URL baseURL = null;

    @SuppressWarnings("WeakerAccess")
    public class SimpleNamespaceContext implements NamespaceContext {

        private final Map<String, String> PREF_MAP = new HashMap<>();

        public SimpleNamespaceContext() {

            PREF_MAP.put("ansi-nist", "http://niem.gov/niem/ansi-nist/2.0" );
            PREF_MAP.put("hone-1", "http://homeoffice.gov.uk/hone-1/2010" );
            PREF_MAP.put("itl", "http://biometrics.nist.gov/standard/2-2008" );
            PREF_MAP.put("nc", "http://niem.gov/niem/niem-core/2.0");
            PREF_MAP.put("s", "http://niem.gov/niem/structures/2.0");
            PREF_MAP.put("soap", "http://www.w3.org/2003/05/soap-envelope");
            PREF_MAP.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        }

        public SimpleNamespaceContext(Properties namespaces) {

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


    @org.junit.jupiter.api.BeforeEach
    void setUpBaseResourceDir(){

        baseURL = this.getClass().getResource("/");
    }


    @Test
    void saticValidator() throws Exception {

        StaticValidatorStore vs = new StaticValidatorStore(
                "/soap:Envelope/soap:Header/hone-1:HONETransactionHeader/ansi-nist:TransactionCategoryCode",
                new SimpleNamespaceContext(),
                Paths.get(baseURL.getPath(), "validatorStore/schemas"));


        FileInputStream fs = new FileInputStream( Paths.get(baseURL.getPath(), "validatorStore/xml/FAS.xml").toFile());
        Validator validator = vs.selectValidator(fs);

        assertNotNull(validator);

        vs.getVAL_MAP().forEach((k,val) -> System.out.println("loaded val for " +k) );

    }

    @Test
    void templatedValidator() throws Exception {

        TemplatedValdiatorStore vs = new TemplatedValdiatorStore(Paths.get(baseURL.getPath(), "validatorStore/templates"));

        FileInputStream fs = new FileInputStream( Paths.get(baseURL.getPath(), "validatorStore/xml/FAS.xml").toFile());
        Validator val = vs.selectValidator(fs);

        assertNotNull(val);

    }




}