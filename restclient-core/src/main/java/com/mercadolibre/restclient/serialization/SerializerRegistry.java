package com.mercadolibre.restclient.serialization;

import com.mercadolibre.restclient.http.ContentType;

import java.util.Map;


public interface SerializerRegistry {

    Map<ContentType,Serializer> getMappings();

}
