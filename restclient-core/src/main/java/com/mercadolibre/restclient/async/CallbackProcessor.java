package com.mercadolibre.restclient.async;

import com.mercadolibre.restclient.Request;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.exception.RestException;

public interface CallbackProcessor<T> {

    Response makeResponse(Request r, T response) throws RestException;

}
