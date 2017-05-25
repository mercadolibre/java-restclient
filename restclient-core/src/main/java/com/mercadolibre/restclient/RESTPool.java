package com.mercadolibre.restclient;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;

import com.mercadolibre.restclient.util.CoberturaIgnore;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.mercadolibre.restclient.cache.RESTCache;
import com.mercadolibre.restclient.http.Authentication;
import com.mercadolibre.restclient.http.ContentType;
import com.mercadolibre.restclient.http.Proxy;
import com.mercadolibre.restclient.interceptor.AcceptInterceptor;
import com.mercadolibre.restclient.interceptor.AddTimeInterceptor;
import com.mercadolibre.restclient.interceptor.ContentTypeInterceptor;
import com.mercadolibre.restclient.interceptor.RequestInterceptor;
import com.mercadolibre.restclient.interceptor.ResponseInterceptor;
import com.mercadolibre.restclient.retry.NoopRetryStrategy;
import com.mercadolibre.restclient.retry.RetryStrategy;

/**
 * Core pool implementation. It contains connection and HTTP parameters as well as feature specifications.
 */
public class RESTPool {

    public static final RESTPool DEFAULT = makeDefault();

    public static final int DEFAULT_MAX_TOTAL = 50;
    public static final int DEFAULT_MAX_PER_ROUTE = 50;
    public static final long DEFAULT_MAX_CONNECTION_TIMEOUT = 1000L;
    public static final long DEFAULT_MAX_SOCKET_TIMEOUT = 1000L;
    public static final long DEFAULT_MAX_POOL_WAIT = 100L;
    public static final long DEFAULT_MAX_IDLE_TIME = 30000L;
    public static final long DEFAULT_EVICTOR_SLEEP = (long) (1000*10);
    public static final boolean DEFAULT_FOLLOW_REDIRECTS = false;
    public static final boolean DEFAULT_COMPRESSION = false;
    public static final int DEFAULT_VALIDATION_ON_INACTIVITY = -1;
    public static final int DEFAULT_REACTOR_THREAD_COUNT = Runtime.getRuntime().availableProcessors();

    private int maxTotal = DEFAULT_MAX_TOTAL;
    private int maxPerRoute = DEFAULT_MAX_PER_ROUTE;
    private long connectionTimeout = DEFAULT_MAX_CONNECTION_TIMEOUT;
    private long socketTimeout = DEFAULT_MAX_SOCKET_TIMEOUT;
    private long maxPoolWait = DEFAULT_MAX_POOL_WAIT;
    private long evictorSleep = DEFAULT_EVICTOR_SLEEP;
    private long maxIdleTime = DEFAULT_MAX_IDLE_TIME;
    private boolean followRedirects = DEFAULT_FOLLOW_REDIRECTS;
    private boolean compression = DEFAULT_COMPRESSION;
    private String baseURL = null;
    private int validationOnInactivity = DEFAULT_VALIDATION_ON_INACTIVITY;
    private int reactorThreadCount = Runtime.getRuntime().availableProcessors();

    private String name;
    private Proxy proxy;
    private Authentication authentication;
    private RetryStrategy retryStrategy = NoopRetryStrategy.INSTANCE;
    private Deque<RequestInterceptor> requestInterceptors = new LinkedList<>();
    private Deque<ResponseInterceptor> responseInterceptors = new LinkedList<>();
    private RESTCache cache;
    private boolean connectionMetrics = false;
    private boolean expectContinue = true;

    /**
     * Builder implementation for {@link RESTPool}
     */
    public static class Builder {

        public static final Deque<RequestInterceptor> DEFAULT_INTERCEPTORS = new LinkedList<>(Arrays.asList(
                AddTimeInterceptor.INSTANCE,
                new ContentTypeInterceptor(ContentType.APPLICATION_JSON, false),
                new AcceptInterceptor(false, ContentType.APPLICATION_JSON.getMimeType())
        ));

        private RESTPool pool;

        private Builder() {
        	this.pool = new RESTPool();

            for (RequestInterceptor i : DEFAULT_INTERCEPTORS)
                addInterceptorLast(i);
        }

        /**
         * Specifies the maximum number of connections (active and idle) to be handled.
         * @param maxTotal the max number of connections
         * @return this builder
         */
        public Builder withMaxTotal(int maxTotal) {
            pool.maxTotal = maxTotal;
            return this;
        }

        /**
         * Specifies the maximum number of connections per route (URL + method).
         * @param maxPerRoute the max number of connections
         * @return this builder
         */
        public Builder withMaxPerRoute(int maxPerRoute) {
            pool.maxPerRoute = maxPerRoute;
            return this;
        }

        /**
         * Specifies a timeout for a connection to be made
         * @param connectionTimeout the connection timeout in ms
         * @return this builder
         */
        public Builder withConnectionTimeout(long connectionTimeout) {
            pool.connectionTimeout = connectionTimeout;
            return this;
        }

        /**
         * Specifies a socket transfer timeout
         * @param socketTimeout the socket timeout in ms
         * @return this builder
         */
        public Builder withSocketTimeout(long socketTimeout) {
            pool.socketTimeout = socketTimeout;
            return this;
        }

        /**
         * Specifies a timeout for a connection to be obtained from its pool
         * @param maxPoolWait the timeout in ms
         * @return this builder
         */
        public Builder withMaxPoolWait(long maxPoolWait) {
            pool.maxPoolWait = maxPoolWait;
            return this;
        }

        /**
         * Specifies this pool name. Mandatory.
         * @param name this pool name
         * @return this builder
         */
        public Builder withName(String name) {
            pool.name = name;
            return this;
        }

        /**
         * Specifies a {@link Proxy} instance to be used by this pool
         * @param proxy a Proxy instance
         * @return this builder
         */
        public Builder withProxy(Proxy proxy) {
            pool.proxy = proxy;
            return this;
        }

        /**
         * Specifies params for BASIC authentication via an {@link Authentication} instance
         * @param authentication an Authentication instance
         * @return this builder
         */
        public Builder withAuthentication(Authentication authentication) {
            pool.authentication = authentication;
            return this;
        }

        /**
         * Specifies a {@link RetryStrategy} to be used by default
         * @param retryStrategy a RetryStrategy instance
         * @return this builder
         */
        public Builder withRetryStrategy(RetryStrategy retryStrategy) {
            pool.retryStrategy = retryStrategy;
            return this;
        }

        /**
         * Adds a {@link RequestInterceptor} first into its deque.
         * @param interceptor a RequestInterceptor instance
         * @return this builder
         */
        public Builder addInterceptorFirst(RequestInterceptor interceptor) {
            pool.requestInterceptors.addFirst(interceptor);
            return this;
        }

        /**
         * Adds a {@link RequestInterceptor} last into its deque.
         * @param interceptor a RequestInterceptor instance
         * @return this builder
         */
        public Builder addInterceptorLast(RequestInterceptor interceptor) {
            pool.requestInterceptors.addLast(interceptor);
            return this;
        }

        /**
         * Adds a {@link ResponseInterceptor} first into its deque.
         * @param interceptor a ResponseInterceptor instance
         * @return this builder
         */
        public Builder addInterceptorFirst(ResponseInterceptor interceptor) {
            pool.responseInterceptors.addFirst(interceptor);
            return this;
        }

        /**
         * Adds a {@link ResponseInterceptor} last into its deque.
         * @param interceptor a ResponseInterceptor instance
         * @return this builder
         */
        public Builder addInterceptorLast(ResponseInterceptor interceptor) {
            pool.responseInterceptors.addLast(interceptor);
            return this;
        }

        /**
         * Specifies whether redirects should be followed automatically. Default is false.
         * @param followRedirects a boolean flag
         * @return this builder
         */
        public Builder withFollowRedirects(boolean followRedirects) {
            pool.followRedirects = followRedirects;
            return this;
        }

        /**
         * Specifies whether gzip/deflate compression should be handled manually. Default is false.
         * @param compression a boolean flag
         * @return this builder
         */
        public Builder withCompression(boolean compression) {
            pool.compression = compression;
            return this;
        }

        /**
         * Specifies a base URL to be used in all requests. This way you could only specify a URI in your requests.
         * @param baseURL a string representing a base URL (protocol + domain + optional resource)
         * @return this builder
         */
        public Builder withBaseURL(String baseURL) {
            pool.baseURL = baseURL.replaceFirst("(.+?)/$","$1");
            return this;
        }

        /**
         * Specifies a cache to be used in GET requests
         * @param cache a {@link RESTCache} instance
         * @return this builder
         */
        public Builder withCache(RESTCache cache) {
            pool.cache = cache;
            return this;
        }

        /**
         * Specifies whether connection metrics (available, leased, and pending) should be tracked. Default is false
         * @param connectionMetrics a boolean flag
         * @return this builder
         */
        public Builder withConnectionMetrics(boolean connectionMetrics) {
            pool.connectionMetrics = connectionMetrics;
            return this;
        }

        /**
         * Defines a time for the idle connection evictor to sleep between succesive runs
         * @param evictorSleep a sleep time in ms
         * @return this builder
         */
        public Builder withEvictorSleep(long evictorSleep) {
            pool.evictorSleep = evictorSleep;
            return this;
        }

        /**
         * Defines a threshold of inactivity for a connection, over with the idle connection evictor will consider it as idle and marked for eviction.
         * @param maxIdleTime a threshold in ms
         * @return this builder
         */
        public Builder withMaxIdleTime(long maxIdleTime) {
            pool.maxIdleTime = maxIdleTime;
            return this;
        }

        /**
         * Specifies whether Expect: 100-Continue handshake should be used. Default is false
         * @param expectContinue a boolean flag
         * @return this builder
         */
        public Builder withExpectContinue(boolean expectContinue) {
            pool.expectContinue = expectContinue;
            return this;
        }

        /**
         * Specifies an amount of time after which an inactive connection should be rechecked upon lease
         * @param validationOnInactivity maximum inactive time without validation in seconds. A negative value disables this feature.
         * @return this builder
         */
        public Builder withValidationOnInactivity(int validationOnInactivity) {
            pool.validationOnInactivity = validationOnInactivity;
            return this;
        }

        /**
         * Specifies the thread amount the IO reactor will use for handling events.
         * @param reactorThreadCount numbers of threads to be used by reactor.
         * @return this builder
         */
        public Builder withReactorThreadCount(int reactorThreadCount) {
            if (reactorThreadCount <= 0) throw new IllegalArgumentException("Reactor thread count should be positive");

            pool.reactorThreadCount = reactorThreadCount;
            return this;
        }

        /**
         * Builds a {@link RESTPool} with all parameters specified by this builder
         * @return a RESTPool instance
         */
        public RESTPool build() {
            if (StringUtils.isBlank(pool.name))
                throw new IllegalArgumentException("Pool name should not be blank");

            return pool;
        }

    }
    
    private RESTPool() {
    	super();
    }

    /**
     * Gets a builder for a RESTPool
     * @return a {@link com.mercadolibre.restclient.RESTPool.Builder} instance
     */
    public static Builder builder() {
    	return new Builder();
    }

    private static RESTPool makeDefault() {
        return new Builder().withName("__default__").build();
    }

    /**
     * @see com.mercadolibre.restclient.RESTPool.Builder#withMaxTotal(int)
     * @return the amount of max connections for this pool
     */
    public int getMaxTotal() {
        return maxTotal;
    }

    /**
     * @see com.mercadolibre.restclient.RESTPool.Builder#withMaxPerRoute(int)
     * @return the amount of max connectios per route for this pool
     */
    public int getMaxPerRoute() {
        return maxPerRoute;
    }

    /**
     * @see com.mercadolibre.restclient.RESTPool.Builder#withConnectionTimeout(long)
     * @return connection timeout in ms
     */
    public long getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * @see com.mercadolibre.restclient.RESTPool.Builder#withSocketTimeout(long)
     * @return socket timeout in ms
     */
    public long getSocketTimeout() {
        return socketTimeout;
    }

    /**
     * @see com.mercadolibre.restclient.RESTPool.Builder#withMaxPoolWait(long)
     * @return max connection wait in ms
     */
    public long getMaxPoolWait() {
        return maxPoolWait;
    }

    /**
     * @see com.mercadolibre.restclient.RESTPool.Builder#withName(String)
     * @return this pool's name
     */
    public String getName() {
        return name;
    }

    /**
     * @see com.mercadolibre.restclient.RESTPool.Builder#withEvictorSleep(long)
     * @return evictor sleep time in ms
     */
    public long getEvictorSleep() {
        return evictorSleep;
    }

    /**
     * @see com.mercadolibre.restclient.RESTPool.Builder#withMaxIdleTime(long)
     * @return connection min idle time in ms
     */
    public long getMaxIdleTime() {
        return maxIdleTime;
    }

    /**
     * @see com.mercadolibre.restclient.RESTPool.Builder#withProxy(Proxy)
     * @return the {@link Proxy} instance associated with this pool
     */
    public Proxy getProxy() {
        return proxy;
    }

    /**
     * @see com.mercadolibre.restclient.RESTPool.Builder#withAuthentication(Authentication)
     * @return the {@link Authentication} instance associated with this pool
     */
    public Authentication getAuthentication() {
    	return authentication;
    }

    /**
     * @see com.mercadolibre.restclient.RESTPool.Builder#withRetryStrategy(RetryStrategy)
     * @return the {@link RetryStrategy} associated with this pool
     */
    public RetryStrategy getRetryStrategy() {
        return retryStrategy;
    }

    /**
     * @see com.mercadolibre.restclient.RESTPool.Builder#addInterceptorFirst(RequestInterceptor)
     * @see com.mercadolibre.restclient.RESTPool.Builder#addInterceptorLast(RequestInterceptor)
     * @return a Deque of {@link RequestInterceptor}
     */
    public Deque<RequestInterceptor> getRequestInterceptors() {
        return requestInterceptors;
    }

    /**
     * @see com.mercadolibre.restclient.RESTPool.Builder#addInterceptorFirst(ResponseInterceptor)
     * @see com.mercadolibre.restclient.RESTPool.Builder#addInterceptorLast(ResponseInterceptor)
     * @return a Deque of {@link ResponseInterceptor}
     */
    public Deque<ResponseInterceptor> getResponseInterceptors() {
        return responseInterceptors;
    }

    /**
     * @see com.mercadolibre.restclient.RESTPool.Builder#withFollowRedirects(boolean)
     * @return a boolean flag indicating whether redirects should be handled automatically
     */
    public boolean followRedirects() {
        return followRedirects;
    }

    /**
     * @see com.mercadolibre.restclient.RESTPool.Builder#withCompression(boolean)
     * @return a boolean flag indicating whether response compression should be handled manually
     */
    public boolean compression() {
    	return compression;
    }

    /**
     * @see com.mercadolibre.restclient.RESTPool.Builder#withBaseURL(String)
     * @return this pool's base URL
     */
    public String getBaseURL() {
        return baseURL;
    }

    /**
     * @see com.mercadolibre.restclient.RESTPool.Builder#withCache(RESTCache)
     * @return a {@link RESTCache} instance associated with this pool
     */
    public RESTCache getCache() {
        return cache;
    }

    /**
     * @see com.mercadolibre.restclient.RESTPool.Builder#withConnectionMetrics(boolean)
     * @return a boolean flag indicating whether connection metrics should be collected automatically
     */
    public boolean hasConnectionMetrics() {
        return connectionMetrics;
    }

    /**
     * @see com.mercadolibre.restclient.RESTPool.Builder#withExpectContinue(boolean)
     * @return a boolean flag indicating whether to use Expect: 100-Continue handshake
     */
    public boolean expectContinue() {
        return expectContinue;
    }

    /**
     * @see com.mercadolibre.restclient.RESTPool.Builder#withValidationOnInactivity(int)
     * @return seconds after which an inactive connection should be rechecked upon lease
     */
    public int getValidationOnInactivity() {
        return validationOnInactivity;
    }

    /**
     * @see RESTPool.Builder#withReactorThreadCount(int)
     * @return number of threads used by IO reactor to handle events
     */
    public int getReactorThreadCount() {
        return reactorThreadCount;
    }

    @Override @CoberturaIgnore
    public boolean equals(Object o) {
        return this == o || o instanceof RESTPool && name.equals(((RESTPool) o).name);
    }

    @Override @CoberturaIgnore
    public int hashCode() {
        return new HashCodeBuilder(17,31).append(name).hashCode();
    }

    public String toString() {
        return name;
    }

}