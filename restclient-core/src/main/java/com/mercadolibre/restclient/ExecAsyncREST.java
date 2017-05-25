package com.mercadolibre.restclient;

import com.mercadolibre.restclient.async.Callback;
import com.mercadolibre.restclient.exception.RestException;

import java.util.concurrent.Future;


public interface ExecAsyncREST<R> extends ExecCallbackAsyncREST<R> {

    Future<Response> asyncGet(Request r) throws RestException;

    Future<Response> asyncPost(Request r) throws RestException;

    Future<Response> asyncPut(Request r) throws RestException;

    Future<Response> asyncDelete(Request r) throws RestException;

    Future<Response> asyncHead(Request r) throws RestException;

    Future<Response> asyncOptions(Request r) throws RestException;

    Future<Response> asyncPurge(Request r) throws RestException;

    void asyncGet(Request r, Callback<Response> callback) throws RestException;

    void asyncPost(Request r, Callback<Response> callback) throws RestException;

    void asyncPut(Request r, Callback<Response> callback) throws RestException;

    void asyncDelete(Request r, Callback<Response> callback) throws RestException;

    void asyncHead(Request r, Callback<Response> callback) throws RestException;

    void asyncOptions(Request r, Callback<Response> callback) throws RestException;

    void asyncPurge(Request r, Callback<Response> callback) throws RestException;

}
