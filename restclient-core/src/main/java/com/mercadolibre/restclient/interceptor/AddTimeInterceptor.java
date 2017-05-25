package com.mercadolibre.restclient.interceptor;

import com.mercadolibre.restclient.Request;

/**
 * Adds a timestamp to a {@link Request}. It can be used for time profiling upon completion.
 */
public enum AddTimeInterceptor implements RequestInterceptor {
    INSTANCE;

    public static final String NAME = "requestTime";

    @Override
    public void intercept(Request r) {
        r.setAttribute(NAME, System.currentTimeMillis());
    }

}
