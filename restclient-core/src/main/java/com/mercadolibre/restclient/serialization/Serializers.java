package com.mercadolibre.restclient.serialization;

import com.mercadolibre.restclient.http.ContentType;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Holder which maps a {@link ContentType} to a {@link Serializer}
 */
public class Serializers {

    private static ConcurrentMap<String, Serializer> serializers = new ConcurrentHashMap<>();

    /**
     * Registers a Serializer for a ContentType
     * @param t a string representation of the content type
     * @param s a serializer
     */
    public static void register(String t, Serializer s) {
        serializers.put(t,s);
    }

    /**
     * Registers a Serializer for a ContentType
     * @param t a content type
     * @param s a serializer
     */
    public static void register(ContentType t, Serializer s) {
        serializers.put(t.getMimeType(),s);
    }

    /**
     * Resolves a content type and gives its associated serializer
     * @param t a content type
     * @return a serializer
     */
    public static Serializer resolve(ContentType t) {
        return serializers.get(t.getMimeType());
    }

    /**
     * Resolves a content type and gives its associated serializer
     * @param t a string representation of the content type
     * @return a serializer
     */
    public static Serializer resolve(String t) {
        return serializers.get(t);
    }

    /**
     * Clears all mappings
     */
    public static void clear() {
        serializers.clear();
    }

}