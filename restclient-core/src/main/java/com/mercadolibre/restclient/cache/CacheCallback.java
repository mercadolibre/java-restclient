package com.mercadolibre.restclient.cache;

import com.mercadolibre.restclient.Request;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.ResponseCallbackFuture;
import com.mercadolibre.restclient.async.*;

import static com.mercadolibre.restclient.log.LogUtil.log;

/**
 * @author mlabarinas
 */
public class CacheCallback<T extends Response> implements Callback<T> {

	protected Request request;
    private ResponseCallbackFuture future;
	
	public CacheCallback(Request request) {
		this(request, new ResponseCallbackFuture());
	}

	protected CacheCallback(Request request, ResponseCallbackFuture future) {
		this.request = request;
		this.future = future;
	}
	
	public void success(T response) {
		if (response == null) {
			request.byPassCache(true);
			Action.resend(request, writeBackAction());
		} else if (!response.getCacheControl().isExpired()) {
			successAction(response);
		} else if (request.getCache().allowStaleResponse() && response.getCacheControl().isFreshForRevalidate()) {
			request.byPassCache(true);
			StaleRequestQueue.enqueue(request);
    		successAction(response);
    	} else {
			request.byPassCache(true);
			Action.resend(request, errorWriteBackAction(response));
		}
	}

	public void failure(Throwable e) {
		log.error("Failure in cache fetch", e);
		request.byPassCache(true);
		Action.resend(request, writeBackAction());
	}

	protected void successAction(T response) {
		future.setDone(response, null);
	}

	protected HTTPCallback<T> writeBackAction() {
		return new WriteBackHTTPCallback<>(request, future);
	}

	protected HTTPCallback<T> errorWriteBackAction(T response) {
		return new ErrorWriteBackHTTPCallback<>(request, future, response);
	}

	public void cancel() {
		future.setCancelled(true);
	} 
	
    public final ResponseCallbackFuture getFuture() {
        return future;
    }

}
