package com.mercadolibre.restclient.async;

import com.mercadolibre.restclient.Request;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.ResponseCallbackFuture;
import com.mercadolibre.restclient.exception.RestException;


public class ErrorWriteBackHTTPCallback<T> extends HTTPCallback<T>  {

    private Response cachedResponse;

    public ErrorWriteBackHTTPCallback(Request request, ResponseCallbackFuture future, Response cachedResponse) {
        super(request, future);
        this.cachedResponse = cachedResponse;
    }

    private void cacheResult(Response response, RestException exception) {
        if (exception == null)
            request.getCache().internalAsyncPut(request.getURL(), response);
    }

    protected void doCallback(Response response, RestException exception) {
        super.successAction(response, exception);
    }

    protected void doFailure(RestException e) {
        super.failureAction(e);
    }

    @Override
    protected void successAction(Response response, RestException exception) {
    	if (!response.getCacheControl().isExpired() || request.getCache().allowStaleResponse() && response.getCacheControl().isFreshForRevalidate()) {
            cacheResult(response, exception);
            doCallback(response, exception);
        } else if (response.getStatus() / 100 == 5 && request.getCache().allowStaleResponse() && cachedResponse.getCacheControl().isFreshForError() && cachedResponse.getStatus() / 100 != 5) {
            doCallback(cachedResponse, null);
        } else {
            doCallback(response, exception);
        }
    }


    @Override
    protected void failureAction(RestException e) {
        if (request.getCache().allowStaleResponse() && cachedResponse.getCacheControl().isFreshForError())
            doCallback(cachedResponse, null);
        else
            doFailure(e);
    }

}
