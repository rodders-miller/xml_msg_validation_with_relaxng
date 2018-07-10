package com.sussexsoftware.relaxng;

public class ValidatorException extends Exception {
    @SuppressWarnings("unused")
    public ValidatorException() { super(); }
    public ValidatorException(String message) { super(message); }
    public ValidatorException(String message, Throwable cause) { super(message, cause); }
    public ValidatorException(Throwable cause) { super(cause); }
}
