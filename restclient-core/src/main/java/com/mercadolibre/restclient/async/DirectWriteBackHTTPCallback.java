package com.mercadolibre.restclient.async;

import com.mercadolibre.restclient.Request;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.exception.RestException;


public class DirectWriteBackHTTPCallback<T> extends HTTPCallback<T> {

    private Callback<Response> callback;

    public DirectWriteBackHTTPCallback(Request request, Callback<Response> callback) {
        super(request, null);
        this.callback = callback;
    }

    protected void cacheResult(Response response, RestException exception) {
        if (exception == null && response.getStatus() / 100 == 2) {
            request.getCache().internalAsyncPut(request.getURL(), response);
        }
    }

    @Override
    protected void successAction(Response r, RestException e) {
        cacheResult(r, e);
        if (e != null)
            failureAction(e);
        else
            callback.success(r);
    }

    @Override
    protected void failureAction(RestException e) {
        callback.failure(e);
    }

    @Override
    public void cancel() {
        callback.cancel();
    }

    @Override
    protected boolean futureRunning() {
        return true;
    }

}
