package com.mercadolibre.restclient;

import com.mercadolibre.restclient.exception.RestException;

public interface RequestProcessor {

    Response makeResponse(Request request, int run) throws RestException;

}
