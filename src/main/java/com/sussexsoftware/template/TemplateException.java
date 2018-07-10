package com.sussexsoftware.template;

@SuppressWarnings("unused")
public class TemplateException extends Exception {
        public TemplateException() { super(); }
        public TemplateException(String message) { super(message); }
        public TemplateException(String message, Throwable cause) { super(message, cause); }
        public TemplateException(Throwable cause) { super(cause); }
}
