package com.mercadolibre.restclient.httpc;

import org.apache.http.client.methods.HttpRequestBase;

import java.net.URI;

public class HttpPurge extends HttpRequestBase {

    public static final String METHOD_NAME = "PURGE";

    public HttpPurge(String uri) {
        setURI(URI.create(uri));
    }

    public HttpPurge(URI uri) {
        setURI(uri);
    }

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }
}
