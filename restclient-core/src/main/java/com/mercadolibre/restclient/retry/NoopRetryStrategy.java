package com.mercadolibre.restclient.retry;

import com.mercadolibre.restclient.Request;
import com.mercadolibre.restclient.Response;

/**
 * A dummy retry strategy. It'll never retry.
 */
public enum NoopRetryStrategy implements RetryStrategy {
    
	INSTANCE;

    @Override
    public RetryResponse shouldRetry(Request req, Response r, Exception e, int retries) {
        return new RetryResponse(false);
    }

}
