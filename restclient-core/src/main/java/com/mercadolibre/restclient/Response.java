package com.mercadolibre.restclient;

import com.mercadolibre.restclient.cache.CacheControl;
import com.mercadolibre.restclient.exception.ParseException;
import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.http.Header;
import com.mercadolibre.restclient.http.Headers;
import com.mercadolibre.restclient.serialization.Serializer;
import com.mercadolibre.restclient.serialization.Serializers;

import java.io.Serializable;

/**
 * A representation of an HTTP response, returned by all {@link RestClient} HTTP calls.
 */
public class Response implements Serializable {

    private static final long serialVersionUID = 1L;
	
    private int status;
    private Headers headers;
    private byte[] bytes;
    
    private CacheControl cacheControl;

    public Response(int status, Headers headers, byte[] bytes) {
        this.status = status;
        this.headers = headers;
        this.bytes = bytes;
        
        this.cacheControl = CacheControl.builder(headers).build();
    }

    /**
     * Retrieves the HTTP status code for the request
     * @return an int for the status code
     * @see <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html">HTTP/1.1: Status Code Definitions</a>
     */
	public int getStatus() {
        return status;
    }

    /**
     * Retrieves the HTTP headers associated with the response
     * @return a {@link Headers} instance
     */
    public Headers getHeaders() {
        return headers;
    }

    /**
     * Retrieves a header contained in this response by name.
     * @param name header name
     * @return the {@link Header} instance representing the particular header, or null if it doesn't exist
     */
    public Header getHeader(String name) {
        return headers.getHeader(name);
    }

    /**
     * Retrieves raw response body
     * @return a byte array or null if response has no body
     * @see #getString()
     */
    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * Retrieves the Cache-Control header information
     * @return a {@link CacheControl} instance
     */
    public CacheControl getCacheControl() {
    	return cacheControl;
    }

    /**
     * Returns the response body, parsed according to Content-Type header, using registered {@link Serializer serializers}
     * @return An object representing the request body, it should be casted accordingly
     * @throws ParseException if data could not be parsed for this Content-Type
     */
    public Object getData() throws ParseException {
        if (bytes == null) return null;

        ContentType type = ContentType.get(headers);
        Serializer serializer = Serializers.resolve(type);

        if (serializer != null)
            return serializer.parse(bytes, type.getCharset());
        else
            throw new UnsupportedOperationException("Cannot parse elements of type " + type.getMimeType());
    }

    /**
     * Returns the response body, parsed according to Content-Type header, using registered {@link Serializer serializers}
     * @param model a class for the model where response data should be marshalled
     * @param <T> The type of the object to be parsed according to corresponding {@link Serializer}, based on Content-Type
     * @return An instance of the class specified in model
     * @throws ParseException if data could not be parsed for this Content-Type
     */
    public <T> T getData(Class<T> model) throws ParseException {
        if (bytes == null) return null;

        ContentType type = ContentType.get(headers);
        Serializer serializer = Serializers.resolve(type);

        if (serializer != null)
            return serializer.parse(bytes, type.getCharset(), model);
        else
            throw new UnsupportedOperationException("Cannot parse elements of type " + type.getMimeType());
    }

    /**
     * Returns a string representation of the response body
     * @return a string for the response body or null if it is empty
     * @see #getBytes()
     */
    public String getString() {
        if (bytes == null) return null;

        ContentType type = ContentType.get(headers);
        return new String(bytes, type.getCharset());
    }

}