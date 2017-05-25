package com.mercadolibre.restclient.cache.memcached;

import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.cache.RESTCache;

import java.io.IOException;

import static com.mercadolibre.restclient.log.LogUtil.log;

/**
 * @author mlabarinas
 */
public class RESTMemcachedCache extends RESTCache {
	
	private RESTMemcachedClient client;
	
	public RESTMemcachedCache(String name, RESTMemcachedClient client) {
		super(name);
		
		this.client = client;
		
		log.debug("Init memcached cache: " + name);
	}

	public RESTMemcachedCache(String name, RESTMemcachedClient client, RESTCache nextLevel) {
		super(name, nextLevel);

		this.client = client;

		log.debug("Init memcached cache: " + name);
	}

	public Response get(String url) {
		return client.get(url);
	}

	public void put(String url, Response response) {
		client.put(url, response);
	}

	public void evict(String url) {
		client.delete(url);
	}

	public void evictAll() {
		client.flush();
	}

	@Override
	public void close() throws IOException {
		client.close();
	}

}