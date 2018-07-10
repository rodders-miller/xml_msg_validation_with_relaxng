package com.sussexsoftware.template;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class SchemaGeneratorTest {


    private URL baseURL = null;

    @BeforeEach
    void setUpBaseResourceDir(){

        baseURL = this.getClass().getResource("/");
    }


    @Test
    void applyTemplate() throws Exception {

        SchemaGenerator gen = new SchemaGenerator( Paths.get(baseURL.getPath(), "schemaGenerator/templates"));
        Map<String, Object> values = new HashMap<>();
        ArrayList<String>  operations = new ArrayList<>();

        operations.add("PO_1_WL");
        operations.add("PO_1_1");

        values.put("operations", operations);

        InputStream instream = gen.applyTemplate("FAS",values);
        String readLine;
        BufferedReader br = new BufferedReader(new InputStreamReader(instream));

        while (((readLine = br.readLine()) != null)) {
            System.out.println(readLine);
        }

        operations.add("PO_1_M");

        instream = gen.applyTemplate("FAS",values);
        br = new BufferedReader(new InputStreamReader(instream));

        while (((readLine = br.readLine()) != null)) {
            System.out.println(readLine);
        }

    }
}