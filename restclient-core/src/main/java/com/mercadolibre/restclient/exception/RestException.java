package com.mercadolibre.restclient.exception;

public class RestException extends Exception {

    private static final long serialVersionUID = 1L;
	
    private String body;

    public RestException(String message) {
        super(message);
    }
    
    public RestException(Throwable cause) {
        super(cause);
    }

    public RestException(String message, String body) {
        super(message);
        this.body = body;
    }

    public RestException(String message, Throwable cause, String body) {
        super(message, cause);
        this.body = body;
    }

    public RestException(Throwable cause, String body) {
        super(cause);
        this.body = body;
    }

    public RestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String body) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.body = body;
    }

    public String getBody() {
        return body;
    }

}