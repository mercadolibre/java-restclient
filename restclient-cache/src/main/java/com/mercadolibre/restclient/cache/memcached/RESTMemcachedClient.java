package com.mercadolibre.restclient.cache.memcached;

import com.mercadolibre.restclient.Response;

import java.io.Closeable;

/**
 * @author mlabarinas
 */
public interface RESTMemcachedClient extends Closeable {

	Response get(String url);
	void put(String url, Response response);
	void delete(String url);
	void flush();
	
}
