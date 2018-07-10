package com.sussexsoftware.relaxng;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

class ValidatorTest {

    private URL baseURL = null;

    @org.junit.jupiter.api.BeforeEach
    void setUpBaseResourceDir(){

        baseURL = this.getClass().getResource("/");
    }


   // @org.junit.jupiter.api.Test
    void validateInStream() throws Exception {

        String fileName = baseURL + "/validator/validXML/FAS.xml";
        String schemaName = baseURL + "/validator/schema/schema.rng";

        URL fileURL = new URL(baseURL,fileName);
        File file = Paths.get(fileURL.toURI()).toFile();
        FileInputStream fileInputStream = new FileInputStream(file);

        Validator val = new Validator();
        val.setSchema(schemaName);
        Validator.ValidationResult result = val.validateIns(fileInputStream);

        assert(result.hasError);
        assert(result.error.length() == 0);


    }

    @org.junit.jupiter.api.Test
    void validateAllValidFAS() throws Exception {

        String schemaName = baseURL + "/validator/schema/schema.rng";

        Validator val = new Validator();
        val.setSchema(schemaName);

        // Lambda consumer that handles the exception from the
        // I/O operation.
        // Each file is validated against the
        Consumer<Path> consumer = (path)->{

            try {
                InputStream ins = new FileInputStream(path.toFile());
                Validator.ValidationResult result = val.validateIns(ins);

                if( result.hasError )
                {
                    System.err.println(result.error);
                }

                assert(!result.hasError);
                assert(result.error.length() == 0);

            } catch (Exception exp) {
                assert(false);
            }
        };

        Files.newDirectoryStream(Paths.get(baseURL.getPath(),"/validator/validXML/"), path -> path.toString().endsWith(".xml"))
                .forEach(consumer);
    }

    @org.junit.jupiter.api.Test
    void validateAllinvalidFAS() throws Exception {

        String schemaName = baseURL + "/validator/schema/schema.rng";

        Validator val = new Validator();
        val.setSchema(schemaName);

        // Lambda consumer that handles the exception from the
        // I/O operation.
        // Each file is validated against the
        Consumer<Path> consumer = (path)->{

            try {
                InputStream ins = new FileInputStream(path.toFile());
                Validator.ValidationResult result = val.validateIns(ins);

                assert(result.hasError);
                assert(result.error.length() > 0);
                System.out.println(result.error);

            } catch (Exception exp) {
                assert(false);
            }
        };

        Files.newDirectoryStream(Paths.get(baseURL.getPath(),"/validator/invalidXML/"), path -> path.toString().endsWith(".xml"))
                .forEach(consumer);
    }

    }