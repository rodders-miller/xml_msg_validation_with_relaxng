package com.sussexsoftware.relaxng;

import java.io.InputStream;

@SuppressWarnings("WeakerAccess")
public interface ValidatorStore {

    Validator selectValidator(InputStream xmlInStm) throws ValidatorException;

}
