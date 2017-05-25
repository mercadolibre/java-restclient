package com.mercadolibre.restclient.httpc.async;


import com.mercadolibre.restclient.async.HTTPCallback;

import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;


public class HTTPCCallback implements FutureCallback<HttpResponse> {

    private HTTPCallback<HttpResponse> callback;

    public HTTPCCallback(HTTPCallback<HttpResponse> callback) {
        this.callback = callback;
    }

    @Override
    public void completed(HttpResponse httpResponse) {
        callback.success(httpResponse);
    }

    @Override
    public void failed(Exception e) {
        callback.failure(e);
    }

    @Override
    public void cancelled() {
        callback.cancel();
    }
	
}
