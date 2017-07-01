package com.mercadolibre.restclient.async;

import com.mercadolibre.restclient.Request;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.exception.RestException;


public class DirectErrorWriteBackHTTPCallback<T> extends ErrorWriteBackHTTPCallback<T>  {

    Callback<Response> callback;

    public DirectErrorWriteBackHTTPCallback(Request request, Callback<Response> callback, Response cachedResponse) {
        super(request, null, cachedResponse);
        this.callback = callback;
    }

    @Override
    protected void doCallback(Response response, RestException exception) {
        if (exception != null)
            doFailure(exception);
        else
            callback.success(response);
    }

    @Override
    protected void doFailure(RestException e) {
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
