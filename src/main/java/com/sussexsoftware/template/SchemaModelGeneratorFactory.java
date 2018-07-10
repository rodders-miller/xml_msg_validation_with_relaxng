package com.sussexsoftware.template;

import javax.xml.namespace.NamespaceContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;

public class SchemaModelGeneratorFactory {

    private final Map<String, SchemaModelGenerator> GEN_MAP = new HashMap<>();

    public SchemaModelGeneratorFactory(NamespaceContext nsc, Path modelFilesPath) throws IOException {

        // Lambda consumer that handles the exception from the
        // I/O operation.
        // Each file is validated against the
        Consumer<Path> consumer = (path)->{

            try {

                Properties props = new Properties();
                FileInputStream in = new FileInputStream(path.toFile());
                props.load(in);

                SchemaModelGenerator sgm = new SchemaModelGenerator(props, nsc);

                String fileNameWithOutExt =  path.toFile().getName().replaceFirst("[.][^.]+$", "");
                this.GEN_MAP.put(fileNameWithOutExt, sgm);

            } catch (Exception exp) {
                // Todo tidy up error handling here

                exp.printStackTrace();
            }
        };

        Files.newDirectoryStream(modelFilesPath, path -> path.toString().endsWith(".mod"))
                .forEach(consumer);

    }


    public SchemaModelGenerator getSchemaModelGenerator(String type)
    {
      return GEN_MAP.get(type);
    }

}
