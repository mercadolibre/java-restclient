package com.mercadolibre.restclient;

import com.mercadolibre.restclient.async.Callback;
import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.Headers;

import java.util.concurrent.Future;


public interface AsyncREST {

    Future<Response> asyncGet(String url) throws RestException;
    Future<Response> asyncGet(String url, Headers headers) throws RestException;

    Future<Response> asyncPost(String url) throws RestException;
    Future<Response> asyncPost(String url, Headers headers) throws RestException;
    Future<Response> asyncPost(String url, byte[] body) throws RestException;
    Future<Response> asyncPost(String url, Headers headers, byte[] body) throws RestException;

    Future<Response> asyncPut(String url) throws RestException;
    Future<Response> asyncPut(String url, Headers headers) throws RestException;
    Future<Response> asyncPut(String url, byte[] body) throws RestException;
    Future<Response> asyncPut(String url, Headers headers, byte[] body) throws RestException;

    Future<Response> asyncDelete(String url) throws RestException;
    Future<Response> asyncDelete(String url, Headers headers) throws RestException;

    Future<Response> asyncHead(String url) throws RestException;
    Future<Response> asyncHead(String url, Headers headers) throws RestException;

    Future<Response> asyncOptions(String url) throws RestException;
    Future<Response> asyncOptions(String url, Headers headers) throws RestException;

    Future<Response> asyncPurge(String url) throws RestException;
    Future<Response> asyncPurge(String url, Headers headers) throws RestException;


    void asyncGet(String url, Callback<Response> callback) throws RestException;
    void asyncGet(String url, Headers headers, Callback<Response> callback) throws RestException;

    void asyncPost(String url, Callback<Response> callback) throws RestException;
    void asyncPost(String url, Headers headers, Callback<Response> callback) throws RestException;
    void asyncPost(String url, byte[] body, Callback<Response> callback) throws RestException;
    void asyncPost(String url, Headers headers, byte[] body, Callback<Response> callback) throws RestException;

    void asyncPut(String url, Callback<Response> callback) throws RestException;
    void asyncPut(String url, Headers headers, Callback<Response> callback) throws RestException;
    void asyncPut(String url, byte[] body, Callback<Response> callback) throws RestException;
    void asyncPut(String url, Headers headers, byte[] body, Callback<Response> callback) throws RestException;

    void asyncDelete(String url, Callback<Response> callback) throws RestException;
    void asyncDelete(String url, Headers headers, Callback<Response> callback) throws RestException;

    void asyncHead(String url, Callback<Response> callback) throws RestException;
    void asyncHead(String url, Headers headers, Callback<Response> callback) throws RestException;

    void asyncOptions(String url, Callback<Response> callback) throws RestException;
    void asyncOptions(String url, Headers headers, Callback<Response> callback) throws RestException;

    void asyncPurge(String url, Callback<Response> callback) throws RestException;
    void asyncPurge(String url, Headers headers, Callback<Response> callback) throws RestException;
    

}