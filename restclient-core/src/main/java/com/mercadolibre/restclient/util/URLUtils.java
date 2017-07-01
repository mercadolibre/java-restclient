package com.mercadolibre.restclient.util;

public class URLUtils {

    public static String completeURL(String baseURL, String uri) {
        return baseURL == null || uri.startsWith("http") ? uri : baseURL + uri;
    }

}
