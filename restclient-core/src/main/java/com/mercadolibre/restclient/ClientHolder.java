package com.mercadolibre.restclient;

import com.mercadolibre.restclient.cache.RESTCache;
import com.mercadolibre.restclient.exception.RestException;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ClientHolder implements Closeable {

	private final ConcurrentMap<String, Clients<?>> clients;
	private final ConcurrentMap<String, RESTCache> caches;
	
	public ClientHolder() {
		this.clients = new ConcurrentHashMap<>();
		this.caches = new ConcurrentHashMap<>();
	}
	
    public static class Clients<R> implements Closeable {
    	
        private ExecREST syncClient;
        private ExecAsyncREST<R> asyncClient;
        private RESTPool pool;

        public Clients(ExecREST syncClient, ExecAsyncREST<R> asyncClient, RESTPool pool) {
            this.syncClient = syncClient;
            this.asyncClient = asyncClient;
            this.pool = pool;
        }

        public ExecREST getSyncClient() {
            return syncClient;
        }

        public ExecAsyncREST<R> getAsyncClient() {
            return asyncClient;
        }

        public RESTPool getPool() {
            return pool;
        }

        @Override
        public void close() throws IOException {
            syncClient.close();
            asyncClient.close();
        }
    }
    
    public ExecREST getClient(String pool) throws RestException {
        return getClients(pool).getSyncClient();
    }

    public ExecAsyncREST<?> getAsyncClient(String pool) throws RestException {
        return getClients(pool).getAsyncClient();
    }

    @SuppressWarnings("unchecked")
	public <R> Clients<R> getClients(String pool) throws RestException {
        Clients<R> client = (Clients<R>) clients.get(pool);
        if (client == null) throw new RestException("Missing client for pool " + pool);

        return client;
    }

    public ExecREST getDefaultClient() {
        return getDefaultClients().getSyncClient();
    }

    @SuppressWarnings("unchecked")
    public <R> ExecAsyncREST<R> getDefaultAsyncClient() {
        return (ExecAsyncREST<R>) getDefaultClients().getAsyncClient();
    }

	public <R> Clients<R> getDefaultClients() {
        try {
            return getClients(RESTPool.DEFAULT.getName());
        } catch (RestException e) {
            throw new RuntimeException(e);
        }
    }

    protected <R> void registerPool(RESTPool pool, ExecREST sync, ExecAsyncREST<R> async) {
        if (sync == null || async == null)
            throw new IllegalArgumentException("Clients should not be null");

        if (clients.putIfAbsent(pool.getName(), new Clients<>(sync,async,pool)) != null)
            throw new RuntimeException("Rest client already registered for pool " + pool);
    }
    
    protected void registerCache(RESTCache cache) {
        if (cache == null)
            throw new IllegalArgumentException("Cache should not be null");

        caches.put(cache.getName(), cache);
    }

    public RESTCache getCache(String name) {
        return caches.get(name);
    }

    @Override
    public void close() throws IOException {
        for (Clients c : clients.values()) {
            c.close();
        }

        for (RESTCache c : caches.values()) {
            c.close();
        }
    }
}