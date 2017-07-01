package com.mercadolibre.restclient.cache;

import com.mercadolibre.restclient.Request;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.async.*;

public class DirectCacheCallback<R extends Response> extends CacheCallback<R> {

    private final Callback<Response> target;

    public DirectCacheCallback(Request request, Callback<Response> target) {
        super(request, null);
        this.target = target;
    }

    @Override
    protected void successAction(R response) {
        target.success(response);
    }

    @Override
    public void cancel() {
        target.cancel();
    }

    @Override
    protected HTTPCallback<R> writeBackAction() {
        return new DirectWriteBackHTTPCallback<>(request, target);
    }

    @Override
    protected HTTPCallback<R> errorWriteBackAction(R response) {
        return new DirectErrorWriteBackHTTPCallback<>(request, target, response);
    }

}
