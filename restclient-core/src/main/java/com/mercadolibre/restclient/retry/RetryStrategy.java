package com.mercadolibre.restclient.retry;

import com.mercadolibre.restclient.Request;
import com.mercadolibre.restclient.Response;
import com.mercadolibre.restclient.http.HttpMethod;

import java.util.EnumSet;
import java.util.Set;

import static com.mercadolibre.restclient.http.HttpMethod.*;

/**
 * Abstraction for retry strategies
 */
public interface RetryStrategy {

    /**
     * Default HTTP methods eligible for retrying: GET, HEAD, OPTIONS.
     */
    Set<HttpMethod> DEFAULT_RETRIABLE_METHODS = EnumSet.of(GET, HEAD, OPTIONS);

    /**
     * Informs whether a retry should be retried, based on request and response data. It also gives a delay that should be applied between consecutive retries.
     * @param req the underlying request
     * @param r the response of last execution of the request
     * @param e an exception, if thrown by request execution
     * @param retries retries done so far for this request
     * @return a {@link RetryResponse} instance
     */
    RetryResponse shouldRetry(Request req, Response r, Exception e, int retries);

}
