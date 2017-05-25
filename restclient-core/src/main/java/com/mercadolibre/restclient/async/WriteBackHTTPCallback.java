package com.mercadolibre.restclient.async;

import com.mercadolibre.restclient.Request;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.ResponseCallbackFuture;
import com.mercadolibre.restclient.exception.RestException;


public class WriteBackHTTPCallback<T> extends HTTPCallback<T> {

    public WriteBackHTTPCallback(Request request, ResponseCallbackFuture future) {
        super(request, future);
    }

    protected void cacheResult(Response response, RestException exception) {
        if (exception == null && response.getStatus() / 100 == 2) {
            request.getCache().internalAsyncPut(request.getURL(), response);
        }
    }

    @Override
    protected void successAction(Response r, RestException e) {
        cacheResult(r, e);
        super.successAction(r,e);
    }

}
