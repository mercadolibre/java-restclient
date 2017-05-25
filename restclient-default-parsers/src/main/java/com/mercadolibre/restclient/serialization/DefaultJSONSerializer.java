package com.mercadolibre.restclient.serialization;

import java.nio.charset.Charset;

import com.mercadolibre.json.JsonUtils;
import com.mercadolibre.json.exception.JsonException;
import com.mercadolibre.restclient.exception.ParseException;

/**
 * Default JSON parser/serializer based on Jackson. Implements underscore transformation as well as ISO dates.
 */
public enum DefaultJSONSerializer implements Serializer {
    
	INSTANCE;

    DefaultJSONSerializer() { }

    @Override
    public Object parse(byte[] bytes, Charset charset) throws ParseException {
        try {
            return JsonUtils.INSTANCE.toObject(new String(bytes, charset), Object.class);
        } catch (JsonException e) {
            throw new ParseException(e);
        }
    }

    @Override
    public <T> T parse(byte[] bytes, Charset charset, Class<T> model) throws ParseException {
        try {
            return JsonUtils.INSTANCE.toObject(new String(bytes, charset), model);
        } catch (JsonException e) {
            throw new ParseException(e);
        }
    }

    @Override
    public byte[] serialize(Object o, Charset charset) throws ParseException {
        try {
            return JsonUtils.INSTANCE.toJsonString(o).getBytes();
            
        } catch (JsonException e) {
            throw new ParseException(e);
        }
    }
}
