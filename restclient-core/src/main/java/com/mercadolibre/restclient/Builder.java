package com.mercadolibre.restclient;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A builder for RestClients, given {@link RESTPool} instances
 * @param <T> This builder concrete type
 * @param <R> Concrete response type for implementation
 */
public abstract class Builder<T extends Builder<T,R>, R> {
    
	private RestClient restClient;
	private boolean disableDefault = false;

    private Set<RESTPool> pools;

    public Builder() {
    	this.pools = new LinkedHashSet<>();
    }
    
    protected Builder(RestClient restClient) {
    	this();
        this.restClient = restClient;
    }

    /**
     * Adds a RESTPool to a RestClient
     * @param pools a {@link RESTPool instance}
     * @return this builder
     */
	public Builder<T,R> withPool(RESTPool... pools) {
        Collections.addAll(this.pools, pools);

        return this;
    }

    /**
     * Instructs this builder to disable/enable default RESTPool. If set, at least one pool must be defined explicitly
     * @param disableDefault a boolean flag indicating to disable default RESTPool
     * @return this builder
     */
    public Builder<T,R> disableDefault(boolean disableDefault) {
        this.disableDefault = disableDefault;
        return this;
    }

    /**
     * Instructs this builder to disable default RESTPool. If set, at least one pool must be defined explicitly
     * @return this builder
     */
    public Builder<T,R> disableDefault() {
        return disableDefault(true);
    }

    /**
     * Builds a RestClient according to builder instance specifications
     * @return a {@link RestClient} instance
     * @throws IOException if RestClient could not be built
     */
    public RestClient build() throws IOException {
        if (!disableDefault) pools.add(RESTPool.DEFAULT);

        if (pools.isEmpty()) throw new IllegalStateException("RestClient should have al least one pool");

        for (RESTPool pool : pools) {
            ExecREST client = resolveClient(pool);
            ExecAsyncREST<R> asyncClient = resolveAsyncClient(pool);

            restClient.getHolder().registerPool(pool, client, asyncClient);

            if (pool.getCache() != null) restClient.getHolder().registerCache(pool.getCache());
        }

        return restClient;
    }

    private ExecREST resolveClient(RESTPool pool) throws IOException {
        ExecREST client = buildClient(pool);
        return new WrappingExecREST(client);
    }

    private ExecAsyncREST<R> resolveAsyncClient(RESTPool pool) throws IOException {
        ExecCallbackAsyncREST<R> client = buildAsyncClient(pool);
        return new WrappingExecAsyncREST<>(client);
    }

    public abstract T newInstance(RestClient client);

    protected abstract ExecREST buildClient(RESTPool pool) throws IOException;

    protected abstract ExecCallbackAsyncREST<R> buildAsyncClient(RESTPool pool) throws IOException;

}
