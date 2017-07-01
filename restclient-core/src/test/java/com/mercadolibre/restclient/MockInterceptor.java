package com.mercadolibre.restclient;

import com.mercadolibre.restclient.exception.RestException;

public interface MockInterceptor {

    void intercept(Request request, Response response, RestException exception);

}
