package com.mercadolibre.restclient;

import com.mercadolibre.restclient.async.Callback;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.Headers;

import java.io.OutputStream;
import java.util.concurrent.Future;

public interface StreamAsyncREST {

    Future<Response> asyncGet(String url, OutputStream outputStream) throws RestException;
    Future<Response> asyncGet(String url, Headers headers, OutputStream outputStream) throws RestException;

    Future<Response> asyncPost(String url, OutputStream outputStream) throws RestException;
    Future<Response> asyncPost(String url, Headers headers, OutputStream outputStream) throws RestException;
    Future<Response> asyncPost(String url, byte[] body, OutputStream outputStream) throws RestException;
    Future<Response> asyncPost(String url, Headers headers, byte[] body, OutputStream outputStream) throws RestException;

    Future<Response> asyncPut(String url, OutputStream outputStream) throws RestException;
    Future<Response> asyncPut(String url, Headers headers, OutputStream outputStream) throws RestException;
    Future<Response> asyncPut(String url, byte[] body, OutputStream outputStream) throws RestException;
    Future<Response> asyncPut(String url, Headers headers, byte[] body, OutputStream outputStream) throws RestException;

    void asyncGet(String url, OutputStream outputStream, Callback<Response> callback) throws RestException;
    void asyncGet(String url, Headers headers, OutputStream outputStream, Callback<Response> callback) throws RestException;

    void asyncPost(String url, OutputStream outputStream, Callback<Response> callback) throws RestException;
    void asyncPost(String url, Headers headers, OutputStream outputStream, Callback<Response> callback) throws RestException;
    void asyncPost(String url, byte[] body, OutputStream outputStream, Callback<Response> callback) throws RestException;
    void asyncPost(String url, Headers headers, byte[] body, OutputStream outputStream, Callback<Response> callback) throws RestException;

    void asyncPut(String url, OutputStream outputStream, Callback<Response> callback) throws RestException;
    void asyncPut(String url, Headers headers, OutputStream outputStream, Callback<Response> callback) throws RestException;
    void asyncPut(String url, byte[] body, OutputStream outputStream, Callback<Response> callback) throws RestException;
    void asyncPut(String url, Headers headers, byte[] body, OutputStream outputStream, Callback<Response> callback) throws RestException;

}