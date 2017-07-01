package com.mercadolibre.restclient.cache.kvs;

import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.cache.RESTCache;

import java.io.IOException;

import static com.mercadolibre.restclient.log.LogUtil.log;

/**
 * @author mlabarinas
 */
public class RESTKvsCache extends RESTCache {
	
	private RESTKvsClient client;
	
	public RESTKvsCache(String name, RESTKvsClient client) {
		super(name);
		
		this.client = client;
		
		log.debug("Init memcached cache: " + name);
	}

	public RESTKvsCache(String name, RESTKvsClient client, RESTCache nextLevel) {
		super(name, nextLevel);
		this.client = client;
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