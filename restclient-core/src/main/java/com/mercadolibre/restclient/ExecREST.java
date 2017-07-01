package com.mercadolibre.restclient;

import com.mercadolibre.restclient.exception.RestException;

import java.io.Closeable;


public interface ExecREST extends Closeable {

    Response get(Request r) throws RestException;

    Response post(Request r) throws RestException;

    Response put(Request r) throws RestException;

    Response delete(Request r) throws RestException;

    Response head(Request rl) throws RestException;

    Response options(Request r) throws RestException;

    Response purge(Request r) throws RestException;

}
