package com.mercadolibre.restclient.async;

import com.mercadolibre.metrics.Metrics;
import com.mercadolibre.restclient.Engine;
import com.mercadolibre.restclient.Request;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.ResponseCallbackFuture;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.interceptor.AddTimeInterceptor;
import com.mercadolibre.restclient.log.LogUtil;
import com.mercadolibre.restclient.metrics.MetricUtil;
import com.mercadolibre.restclient.retry.RetryResponse;
import com.mercadolibre.restclient.util.HttpCompressionHandler;

import static com.mercadolibre.restclient.log.LogUtil.log;


public class HTTPCallback<T> implements Callback<T> {

	protected Request request;
    private ResponseCallbackFuture future;
    private int retries = 0;

    public HTTPCallback(Request request) {
        this.request = request;
        this.future = new ResponseCallbackFuture();

        HttpCompressionHandler.handleRequest(request);
    }

    public HTTPCallback(Request request, ResponseCallbackFuture future) {
        this.request = request;
        this.future = future;
        
        HttpCompressionHandler.handleRequest(request);
    }

    private void logTime(Response r) {
        long delta = System.currentTimeMillis() - (long) request.getAttribute(AddTimeInterceptor.NAME);

        Metrics.INSTANCE.recordExecutionTime("restclient.async.http.time", delta, MetricUtil.getExecutionTags(request, r).toArray());

        if (log.isTraceEnabled()) log.trace(LogUtil.makeTimeLogLine(request, r, delta));
    }

    public final void success(T response) {
        Response r = null;
        RetryResponse retryResponse;
        RestException exception = null;

        try {
            r = Engine.<T>callbackProcessor().makeResponse(request, response);
            logTime(r);
            request.applyResponseInterceptors(r);

            retryResponse = request.getRetryStrategy().shouldRetry(request, r, null, retries++);
        } catch (RestException e) {
            exception = e;
            retryResponse = request.getRetryStrategy().shouldRetry(request, null, e, retries++);
        }

        if (futureRunning() && retryResponse.retry()) {
            Metrics.INSTANCE.incrementCounter("restclient.async.http.retry", MetricUtil.getRequestTags(request).toArray());

            try {
                Thread.sleep(retryResponse.getDelay());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            Action.resend(request, this);
        
        } else {
        	try {
	        	HttpCompressionHandler.handleResponse(request, r);
	        	successAction(r, exception);
        	} catch (RestException e) {
        		successAction(r, e);
        	}
	    }
    }

    
    protected void successAction(Response r, RestException e) {
        future.setDone(r,e);
    }

    protected void failureAction(RestException e) {
        future.setDone(null,e);
    }

    public final void failure(Throwable e) {
        Metrics.INSTANCE.incrementCounter("restclient.async.http.error", MetricUtil.getRequestTags(request).toArray());

        RetryResponse retryResponse = request.getRetryStrategy().shouldRetry(request, null, new Exception(e), retries++);
        if (futureRunning() && retryResponse.retry()) {
            Metrics.INSTANCE.incrementCounter("restclient.async.http.retry", MetricUtil.getRequestTags(request).toArray());
            try {
                Thread.sleep(retryResponse.getDelay());
            } catch (InterruptedException i) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(i);
            }

            Action.resend(request,this);
        } else
            failureAction(new RestException(e, e.getMessage()));
    }

    public void cancel() {
        future.setCancelled(true);
    }

    public final ResponseCallbackFuture getFuture() {
        return future;
    }

    public Request getRequest() {
        return request;
    }

    protected boolean futureRunning() {
        return !future.isCancelled();
    }

}