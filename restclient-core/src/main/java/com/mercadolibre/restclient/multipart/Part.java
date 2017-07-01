package com.mercadolibre.restclient.multipart;

import com.mercadolibre.restclient.http.ContentType;

/**
 * Part abstraction for multipart uploading
 * @param <T> the type of this part content
 */
public interface Part<T> {

    String getName();
    T getContent();
    ContentType getContentType();
    void accept(PartVisitor v);

}
