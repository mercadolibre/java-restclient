package com.mercadolibre.restclient.test;

import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.exception.ParseException;
import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.restclient.serialization.Serializer;
import com.mercadolibre.restclient.serialization.Serializers;
import org.junit.After;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.Collections;

import static org.junit.Assert.assertEquals;


public class ResponseTest {

    @After
    public void after() {
        Serializers.clear();
    }

    @Test
    public void shouldGetData() throws ParseException {
        Serializers.register(ContentType.TEXT_PLAIN, new Serializer() {
            @Override
            public Object parse(byte[] data, Charset charset) throws ParseException {
                return new String(data, charset);
            }

            @SuppressWarnings("unchecked")
			@Override
            public <T> T parse(byte[] data, Charset charset, Class<T> model) throws ParseException {
                if (model == String.class)
                    return (T) parse(data, charset);

                throw new ParseException();
            }

            @Override
            public byte[] serialize(Object data, Charset charset) throws ParseException {
                return data.toString().getBytes(charset);
            }
        });

        Response response = new Response(200, new Headers(Collections.singletonMap("Content-Type","text/plain")), "test".getBytes());

        assertEquals("test", response.getData());

    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldFailForUnrecognizedContent() throws ParseException {
        new Response(200, new Headers(Collections.singletonMap("Content-Type","text/plain")), "test".getBytes()).getData();
    }

    @Test
    public void shouldGetDataForType() throws ParseException {
        Serializers.register(ContentType.TEXT_PLAIN, new Serializer() {
            @Override
            public Object parse(byte[] data, Charset charset) throws ParseException {
                return new String(data, charset);
            }

            @SuppressWarnings("unchecked")
			@Override
            public <T> T parse(byte[] data, Charset charset, Class<T> model) throws ParseException {
                if (model == String.class)
                    return (T) parse(data, charset);

                throw new ParseException();
            }

            @Override
            public byte[] serialize(Object data, Charset charset) throws ParseException {
                return data.toString().getBytes(charset);
            }
        });

        Response response = new Response(200, new Headers(Collections.singletonMap("Content-Type","text/plain")), "test".getBytes());

        assertEquals("test", response.getData(String.class));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldFailForUnrecognizedContentForType() throws ParseException {
        new Response(200, new Headers(Collections.singletonMap("Content-Type","text/plain")), "test".getBytes()).getData(String.class);
    }

}
