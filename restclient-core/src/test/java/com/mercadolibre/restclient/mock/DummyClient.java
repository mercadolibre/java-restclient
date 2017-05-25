package com.mercadolibre.restclient.mock;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.mercadolibre.restclient.*;
import com.mercadolibre.restclient.async.HTTPCallback;
import com.mercadolibre.restclient.exception.RestException;


public class DummyClient implements ExecREST, ExecCallbackAsyncREST<Response> {

    private ListeningExecutorService threadPool = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));

    private RESTPool pool;
    private RequestMockHolder holder;

    public DummyClient(RESTPool pool) {
        this.pool = pool;
        this.holder = RequestMockHolder.INSTANCE;
    }

    @Override
    public void close() throws IOException {
    }

    public RESTPool getPool() {
        return pool;
    }

    @Override
    public Response get(Request r) throws RestException {
        return holder.bestMatch(r);
    }

    @Override
    public Response post(Request r) throws RestException {
        return holder.bestMatch(r);
    }

    @Override
    public Response put(Request r) throws RestException {
        return holder.bestMatch(r);
    }

    @Override
    public Response delete(Request r) throws RestException {
        return holder.bestMatch(r);
    }

    @Override
    public Response head(Request r) throws RestException {
        return holder.bestMatch(r);
    }

    @Override
    public Response options(Request r) throws RestException {
        return holder.bestMatch(r);
    }

    @Override
    public Response purge(Request r) throws RestException {
        return holder.bestMatch(r);
    }

    private Future<Response> executeAsync(ListenableFuture<Response> future, final HTTPCallback<Response> callback) {
        Futures.addCallback(future, new FutureCallback<Response>() {
            @Override
            public void onSuccess(Response response) {
                callback.success(response);
            }

            @Override
            public void onFailure(Throwable throwable) {
                callback.failure(throwable);
            }
        });

        return callback.getFuture();
    }

    @Override
    public Future<Response> asyncGet(final Request r, final HTTPCallback<Response> callback) {
        ListenableFuture<Response> future = threadPool.submit(new Callable<Response>() {
            @Override
            public Response call() throws Exception {
                return get(r);
            }
        });

        return executeAsync(future,callback);
    }

    @Override
    public Future<Response> asyncPost(final Request r, final HTTPCallback<Response> callback) {
        ListenableFuture<Response> future = threadPool.submit(new Callable<Response>() {
            @Override
            public Response call() throws Exception {
                return post(r);
            }
        });

        return executeAsync(future, callback);
    }

    @Override
    public Future<Response> asyncPut(final Request r, final HTTPCallback<Response> callback) {
        ListenableFuture<Response> future = threadPool.submit(new Callable<Response>() {
            @Override
            public Response call() throws Exception {
                return put(r);
            }
        });

        return executeAsync(future, callback);
    }

    @Override
    public Future<Response> asyncDelete(final Request r, final HTTPCallback<Response> callback) {
        ListenableFuture<Response> future = threadPool.submit(new Callable<Response>() {
            @Override
            public Response call() throws Exception {
                return delete(r);
            }
        });

        return executeAsync(future, callback);
    }

    @Override
    public Future<Response> asyncHead(final Request r, final HTTPCallback<Response> callback) {
        ListenableFuture<Response> future = threadPool.submit(new Callable<Response>() {
            @Override
            public Response call() throws Exception {
                return head(r);
            }
        });

        return executeAsync(future, callback);
    }

    @Override
    public Future<Response> asyncOptions(final Request r, final HTTPCallback<Response> callback) {
        ListenableFuture<Response> future = threadPool.submit(new Callable<Response>() {
            @Override
            public Response call() throws Exception {
                return options(r);
            }
        });

        return executeAsync(future, callback);
    }

    @Override
    public Future<Response> asyncPurge(final Request r, final HTTPCallback<Response> callback) {
        ListenableFuture<Response> future = threadPool.submit(new Callable<Response>() {
            @Override
            public Response call() throws Exception {
                return purge(r);
            }
        });

        return executeAsync(future, callback);
    }
}
