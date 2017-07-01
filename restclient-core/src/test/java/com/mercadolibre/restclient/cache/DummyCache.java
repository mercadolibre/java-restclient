package com.mercadolibre.restclient.cache;

import com.mercadolibre.restclient.Response;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class DummyCache extends RESTCache {

    public static final String DEFAULT_NAME = "dummy";

    private ConcurrentMap<String,Response> cache = new ConcurrentHashMap<>();
    private boolean shouldFail = false;
    private boolean shouldThrow = false;

    public DummyCache(String name) {
        super(name);
    }

    public DummyCache(String name, RESTCache nextLevel) {
        super(name, nextLevel);
    }

    @Override
    public void close() throws IOException {
    }

    private static class Holder {
        private static final DummyCache INSTANCE = new DummyCache(DEFAULT_NAME);
    }

    public static DummyCache getDefault() {
        return Holder.INSTANCE;
    }

    @Override
    public Response get(String url) {
        if (shouldThrow) throw new RuntimeException("Cache exception");

        return !shouldFail ? cache.get(url) : null;
    }

    @Override
    public void put(String url, Response response) {
        if (shouldThrow) throw new RuntimeException("Cache exception");

        if (!shouldFail) cache.put(url, response);
    }

    @Override
    public void evict(String url) {
        if (shouldThrow) throw new RuntimeException("Cache exception");

        if (!shouldFail) cache.remove(url);
    }

    @Override
    public void evictAll() {
        if (shouldThrow) throw new RuntimeException("Cache exception");

        if (!shouldFail) cache.clear();
    }

    public void reload() {
        cache.clear();
        shouldFail = false;
        shouldThrow = false;
    }

    public boolean shouldFail() {
        return shouldFail;
    }

    public boolean shouldThrow() {
        return shouldThrow;
    }

    public void setFail(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }

    public void setThrow(boolean shouldThrow) {
        this.shouldThrow = shouldThrow;
    }

    public Set<String> getKeys() {
        return new HashSet<>(cache.keySet());
    }

}
