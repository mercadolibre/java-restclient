package com.mercadolibre.restclient;

import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.Headers;

import java.io.OutputStream;

public interface StreamREST {

    Response get(String url, OutputStream stream) throws RestException;
    Response get(String url, Headers headers, OutputStream stream) throws RestException;

    Response post(String url, OutputStream stream) throws RestException;
    Response post(String url, Headers headers, OutputStream stream) throws RestException;
    Response post(String url, byte[] body, OutputStream stream) throws RestException;
    Response post(String url, Headers headers, byte[] body, OutputStream stream) throws RestException;

    Response put(String url, OutputStream stream) throws RestException;
    Response put(String url, Headers headers, OutputStream stream) throws RestException;
    Response put(String url, byte[] body, OutputStream stream) throws RestException;
    Response put(String url, Headers headers, byte[] body, OutputStream stream) throws RestException;

}
