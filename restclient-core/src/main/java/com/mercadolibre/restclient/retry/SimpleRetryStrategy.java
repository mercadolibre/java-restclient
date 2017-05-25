package com.mercadolibre.restclient.retry;

import com.mercadolibre.restclient.Request;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.http.HttpMethod;

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

/**
 * A simple retry strategy implementation. It'll retry for a fixed number of times and wait a fixed interval between them.
 */
public class SimpleRetryStrategy implements RetryStrategy {

    private int maxRetries;
    private long delay;
    private Set<HttpMethod> allowedMethods;

    public SimpleRetryStrategy() {
    	this.allowedMethods = DEFAULT_RETRIABLE_METHODS;
    }
    
    public SimpleRetryStrategy(int maxRetries, long delay) {
    	this();

        if (maxRetries < 0 || delay < 0)
            throw new IllegalArgumentException("maxRetries and delay must be greater than zero");
    	
        this.maxRetries = maxRetries;
        this.delay = delay;
    }

    public SimpleRetryStrategy(int maxRetries, long delay, HttpMethod... methods) {
    	this(maxRetries, delay);
    	
        this.allowedMethods = EnumSet.copyOf(Arrays.asList(methods));
    }

    @Override
    public RetryResponse shouldRetry(Request req, Response r, Exception e, int retries) {
        boolean retry = retries < maxRetries && (e != null || shouldRetryCode(r.getStatus())) && allowedMethods.contains(req.getMethod());
        return new RetryResponse(retry, delay);
    }

    private boolean shouldRetryCode(int code) {
        return code >= HttpURLConnection.HTTP_INTERNAL_ERROR;
    }

    public Set<HttpMethod> getAllowedMethods() {
        return allowedMethods;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public long getDelay() {
        return delay;
    }
}
