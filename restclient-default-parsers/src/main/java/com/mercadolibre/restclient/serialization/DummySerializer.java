package com.mercadolibre.restclient.serialization;

import com.mercadolibre.restclient.exception.ParseException;

import java.nio.charset.Charset;

public enum DummySerializer implements Serializer {
    INSTANCE;

    @Override
    public Object parse(byte[] bytes, Charset charset) throws ParseException {
        return new String(bytes, charset);
    }

    @SuppressWarnings("unchecked")
	@Override
    public <T> T parse(byte[] bytes, Charset charset, Class<T> clazz) throws ParseException {
        if (clazz == String.class)
            return (T) new String(bytes, charset);
        else
            throw new ParseException("Could not parse string to " + clazz.getCanonicalName());
    }

    @Override
    public byte[] serialize(Object s, Charset charset) throws ParseException {
        return s.toString().getBytes(charset);
    }

}
