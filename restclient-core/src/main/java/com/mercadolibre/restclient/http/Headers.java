package com.mercadolibre.restclient.http;

import com.mercadolibre.restclient.util.CoberturaIgnore;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Iterable representation of HTTP headers
 */
public class Headers implements Iterable<Header> {

    /**
     * Constructs an empty Headers instance
     */
    public Headers() {
    }

    /**
     * Constructs an instance from another, copying all of its contents
     * @param from another Headers instance
     */
    public Headers(Headers from) {
        for (Header h : from) add(h);
    }

    /**
     * Constructs and instance from a headers map.
     * @param from a map containing header name and value
     */
    public Headers(Map<String,String> from) {
        for (Map.Entry<String,String> e : from.entrySet())
            add(e.getKey(), e.getValue());
    }

    private Map<String,Header> headers = new HashMap<>();

    /**
     * Adds a header to this instance
     * @param h a {@link Header} instance
     * @return this instance
     */
    public Headers add(Header h) {
        headers.put(h.getName().toLowerCase(), h);
        return this;
    }

    /**
     * Adds a header to this instance
     * @param name header name
     * @param value header value
     * @return this instance
     */
    public Headers add(String name, String value) {
        return add(new Header(name,value));
    }

    /**
     * @return the iterator for this Iterable
     */
    @Override
    public Iterator<Header> iterator() {
        return headers.values().iterator();
    }

    /**
     * Retrieves a header by name
     * @param name header name
     * @return a {@link Header} instance
     */
    public Header getHeader(String name) {
        return headers.get(name.toLowerCase());
    }

    /**
     * Checks if this instance contains header by name
     * @param name header name
     * @return Returns true only if this instance contains a header with specified name
     */
    public boolean contains(String name) {
    	return headers.containsKey(name.toLowerCase());
    }

    @CoberturaIgnore
    public String toString() {
        return headers.toString();
    }

    @CoberturaIgnore
    public boolean equals(Object o) {
        return this == o || o instanceof Headers && headers.equals(((Headers) o).headers);
    }

    @CoberturaIgnore
    public int hashCode() {
        return headers.hashCode();
    }

    /**
     * Creates a new Headers instance, copying all contents from this one
     * @return the new Headers instance
     */
    public Headers clone() {
        Map<String,String> data = new HashMap<>();
        for (Map.Entry<String,Header> e : headers.entrySet())
            data.put(e.getKey(), e.getValue().getValue());

        return new Headers(new HashMap<>(data));
    }

    /**
     * Checks whether this instance doesn´t contain any {@link Header}
     * @return true if this instance contains no headers, false otherwise
     */
    public boolean isEmpty() {
        return headers.isEmpty();
    }

    /**
     * Returns the amounts of {@link Header} instances contained in this instance
     * @return the header count
     */
    public int size() {
        return headers.size();
    }
    
}