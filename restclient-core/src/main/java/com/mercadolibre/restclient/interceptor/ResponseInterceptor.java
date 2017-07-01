package com.mercadolibre.restclient.interceptor;

import com.mercadolibre.restclient.Response;


public interface ResponseInterceptor {

    void intercept(Response r);

}
