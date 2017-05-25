package com.mercadolibre.restclient.cache.local;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.cache.RESTCache;

import java.io.IOException;
import java.util.concurrent.Future;

import static com.mercadolibre.restclient.log.LogUtil.log;

/**
 * @author mlabarinas
 */
public class RESTLocalCache extends RESTCache {

	private Cache<String, Response> cache;
	
	protected long maxSize;
	
	public RESTLocalCache(String name, long maxSize) {
		super(name);
	
		this.cache = CacheBuilder.newBuilder().maximumSize(maxSize).build();
		
		log.debug("Init local cache: " + name + " with max size: " + maxSize);
	}

	public RESTLocalCache(String name, long maxSize, RESTCache nextLevel) {
		super(name, nextLevel);
		this.maxSize = maxSize;
	}

	public long getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(long maxSize) {
		this.maxSize = maxSize;
	}

	public Response get(String url) {
		return cache.getIfPresent(url);
	}

	public void put(String url, Response response) {
		cache.put(url, response);
	}
	
	public Future<Response> asyncGet(String url) {
		return null;
	}

	public Future<Boolean> asyncPut(String url, Response response) {
		return null;
	}

	public void evict(String url) {
		cache.invalidate(url);
	}

	public void evictAll() {
		cache.invalidateAll();
	}

	@Override
	public void close() throws IOException {
	}
}