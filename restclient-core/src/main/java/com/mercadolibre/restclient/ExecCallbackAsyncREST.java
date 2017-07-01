package com.mercadolibre.restclient;

import com.mercadolibre.restclient.async.HTTPCallback;

import java.io.Closeable;
import java.util.concurrent.Future;


public interface ExecCallbackAsyncREST<R> extends Closeable {

    Future<Response> asyncGet(Request r, HTTPCallback<R> callback);

    Future<Response> asyncPost(Request r, HTTPCallback<R> callback);

    Future<Response> asyncPut(Request r, HTTPCallback<R> callback);

    Future<Response> asyncDelete(Request r, HTTPCallback<R> callback);

    Future<Response> asyncHead(Request r, HTTPCallback<R> callback);

    Future<Response> asyncOptions(Request r, HTTPCallback<R> callback);

    Future<Response> asyncPurge(Request r, HTTPCallback<R> callback);

}
