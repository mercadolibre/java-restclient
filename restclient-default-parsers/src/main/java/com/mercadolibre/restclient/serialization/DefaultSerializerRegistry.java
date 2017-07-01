package com.mercadolibre.restclient.serialization;

import com.mercadolibre.restclient.http.ContentType;

import java.util.HashMap;
import java.util.Map;


public class DefaultSerializerRegistry implements SerializerRegistry {

    @Override
    public Map<ContentType, Serializer> getMappings() {
        Map<ContentType,Serializer> output = new HashMap<>();

        output.put(ContentType.TEXT_PLAIN, DummySerializer.INSTANCE);
        output.put(ContentType.APPLICATION_JSON, DefaultJSONSerializer.INSTANCE);
        output.put(ContentType.TEXT_HTML, DummySerializer.INSTANCE);
        output.put(ContentType.APPLICATION_XML, DummySerializer.INSTANCE);

        return output;
    }

}
