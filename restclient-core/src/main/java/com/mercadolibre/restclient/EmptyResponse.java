package com.mercadolibre.restclient;

import com.mercadolibre.restclient.exception.ParseException;
import com.mercadolibre.restclient.http.Headers;

public class EmptyResponse extends Response {

    private static final long serialVersionUID = 1L;

	public EmptyResponse(int status, Headers headers) {
        super(status, headers, null);
    }

    @Override
    public byte[] getBytes() {
        return null;
    }

    @Override
    public Object getData() throws ParseException {
        return null;
    }

    @Override
    public <T> T getData(Class<T> model) throws ParseException {
        return null;
    }

    @Override
    public String getString() {
        return null;
    }

}
