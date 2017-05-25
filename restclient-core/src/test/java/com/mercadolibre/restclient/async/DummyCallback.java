package com.mercadolibre.restclient.async;

import com.mercadolibre.restclient.Request;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.ResponseCallbackFuture;

public class DummyCallback extends HTTPCallback<Response> {

    public DummyCallback(Request request) {
        super(request);
    }

    public DummyCallback(Request request, ResponseCallbackFuture future) {
        super(request, future);
    }
}
