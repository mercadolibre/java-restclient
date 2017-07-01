package com.mercadolibre.restclient.async;

import com.mercadolibre.restclient.Request;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.mock.MockCallbackProcessor;


public class DummyCallbackProcessor implements MockCallbackProcessor<Response> {

    public static final String THROW = "THROW";

    @Override
    public Response makeResponse(Request r, Response response) throws RestException {
        if (response.getBytes() != null && THROW.equals(new String(response.getBytes())))
            throw new RestException("Async request exception");

        return response;
    }

}
