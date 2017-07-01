package com.mercadolibre.restclient;

import com.mercadolibre.restclient.exception.RestException;
import com.mercadolibre.restclient.http.Headers;


public interface REST {

    Response get(String url) throws RestException;
    Response get(String url, Headers headers) throws RestException;

    Response post(String url, byte[] body) throws RestException;
    Response post(String url, Headers headers, byte[] body) throws RestException;
    Response post(String url) throws RestException;
    Response post(String url, Headers headers) throws RestException;

    Response put(String url, byte[] body) throws RestException;
    Response put(String url, Headers headers, byte[] body) throws RestException;
    Response put(String url) throws RestException;
    Response put(String url, Headers headers) throws RestException;

    Response delete(String url) throws RestException;
    Response delete(String url, Headers headers) throws RestException;

    Response head(String url) throws RestException;
    Response head(String url, Headers headers) throws RestException;

    Response options(String url) throws RestException;
    Response options(String url, Headers headers) throws RestException;

    Response purge(String url) throws RestException;
    Response purge(String url, Headers headers) throws RestException;

}