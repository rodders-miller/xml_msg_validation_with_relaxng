package com.sussexsoftware.relaxng;

import com.thaiopensource.util.PropertyMapBuilder;
import com.thaiopensource.validate.*;
import com.thaiopensource.xml.sax.ErrorHandlerImpl;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Validator {

    private boolean configued = false;
    private ValidationDriver driver = null;
    private ByteArrayOutputStream os = null;

    public Validator() {}


    public void setSchema(InputStream schema) throws ValidatorException
    {
        SchemaReader sr = null;

        os = new ByteArrayOutputStream();
        ErrorHandlerImpl errorImpl = new ErrorHandlerImpl(os);
        PropertyMapBuilder pmb = new PropertyMapBuilder();
        pmb.put(ValidateProperty.ERROR_HANDLER, errorImpl);

        driver = new ValidationDriver(pmb.toPropertyMap());
        InputSource in = new InputSource(schema);

        try {

            configued = driver.loadSchema(in);

            if(!configued)  throw new ValidatorException("Unable to load schema into relaxng dirver for steram");

        }
        catch (Exception exp)
        {
            exp.printStackTrace();
            configued = false;
            throw new ValidatorException("Unable to load schema into relaxng dirver" , exp);
        }
    }


    public void setSchema(String schemaName) throws ValidatorException {
        configued = false;
        SchemaReader sr = null;

        os = new ByteArrayOutputStream();
        ErrorHandlerImpl errorImpl = new ErrorHandlerImpl(os);
        PropertyMapBuilder pmb = new PropertyMapBuilder();
        pmb.put(ValidateProperty.ERROR_HANDLER, errorImpl);

        driver = new ValidationDriver(pmb.toPropertyMap());
        InputSource in = ValidationDriver.uriOrFileInputSource(schemaName);

        try {
            configued = driver.loadSchema(in);

            if(!configued)  throw new ValidatorException("Unable to load schema into relaxng dirver for " + schemaName);
        }
        catch (Exception exp)
        {
            configued = false;
            throw new ValidatorException("Unable to load schema into relaxng dirver" , exp);
        }
    }

    public ValidationResult validateIns(InputStream inStream ) throws ValidatorException {

        ValidationResult result = new ValidationResult();

        try {

            result.hasError = !this.validate(new InputSource(inStream));

            if (result.hasError) {
                result.error = this.os.toString();
            }

        } catch (Exception exp) {
            throw new ValidatorException("Unable to validate against schema due to into relaxng dirver fault", exp);
        }
        return result;
    }

    private boolean validate(InputSource inSrc) throws IOException, SAXException {

        return driver.validate(inSrc);
    }

    public class ValidationResult {
        boolean hasError = false;
        String error = "";
    }
}