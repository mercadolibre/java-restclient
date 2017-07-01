package com.mercadolibre.restclient.interceptor;

import com.mercadolibre.restclient.Request;

public interface RequestInterceptor {

    void intercept(Request r);

}
