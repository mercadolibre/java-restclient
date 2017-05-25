package com.mercadolibre.restclient;

import com.mercadolibre.restclient.exception.RestException;

import java.util.concurrent.*;

public class ResponseCallbackFuture implements Future<Response> {

    private final CountDownLatch latch;
    private Response response;
    private RestException exception;
    private volatile boolean cancelled;
    
    public ResponseCallbackFuture() {
    	this.latch = new CountDownLatch(1);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException("Cancelling not allowed");
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public boolean isDone() {
        return latch.getCount() == 0;
    }

    @Override
    public Response get() throws InterruptedException, ExecutionException {
        latch.await();
        return getOutput();
    }

    @Override
    public Response get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        latch.await(timeout, unit);
        return getOutput();
    }

    private Response getOutput() throws ExecutionException {
        if (exception != null)
            throw new ExecutionException(exception);

        return response;
    }

    public void setDone(Response response, RestException exception) {
        this.response = response;
        this.exception = exception;
        latch.countDown();
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}