package com.mercadolibre.restclient.async;

import com.mercadolibre.restclient.Request;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.exception.RestException;


public class DirectCallback<R> extends HTTPCallback<R> {

    private final Callback<Response> target;

    public DirectCallback(Request request, Callback<Response> target) {
        super(request, null);

        this.target = target;
    }

    @Override
    protected void successAction(Response r, RestException e) {
        if (e != null)
            failureAction(e);
        else
            target.success(r);
    }

    @Override
    protected void failureAction(RestException e) {
        target.failure(e);
    }

    @Override
    protected boolean futureRunning() {
        return true;
    }

    @Override
    public void cancel() {
        target.cancel();
    }

}
